package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

  private final SpendClient spendClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
              if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
                final Optional<UserJson> user = UserExtension.createdUser();
                final String username = user.isPresent()
                    ? user.get().username()
                    : userAnno.username();

                final Optional<CategoryJson[]> categories = CategoryExtension.createdCategories();

                final List<SpendJson> result = new ArrayList<>();
                for (Spend spendAnno : userAnno.spendings()) {
                  CategoryJson matchedCategory = null;
                  if (categories.isPresent()) {
                    for (CategoryJson categoryJson : categories.get()) {
                      if (categoryJson.name().equals(spendAnno.category())) {
                        matchedCategory = categoryJson;
                        break;
                      }
                    }
                  }
                  if (matchedCategory == null) {
                    matchedCategory = new CategoryJson(
                        null,
                        spendAnno.category(),
                        username,
                        false
                    );
                  }

                  SpendJson spendJson = new SpendJson(
                      null,
                      new Date(),
                      matchedCategory,
                      spendAnno.currency(),
                      spendAnno.amount(),
                      spendAnno.description(),
                      username
                  );
                  result.add(spendClient.createSpend(spendJson));
                }

                if (user.isPresent()) {
                  user.get().testData().spendings().addAll(
                      result
                  );
                } else {
                  context.getStore(NAMESPACE).put(
                      context.getUniqueId(),
                      result.stream().toArray(SpendJson[]::new)
                  );
                }
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  public SpendJson[] resolveParameter(ParameterContext parameterContext,
                                         ExtensionContext extensionContext) throws ParameterResolutionException {
    final Optional<UserJson> user = UserExtension.createdUser();
    if (user.isPresent()) {
      return user.get().testData().spendings().toArray(SpendJson[]::new);
    } else return createdSpendings().orElseThrow();
  }

  public static Optional<SpendJson[]> createdSpendings() {
    final ExtensionContext methodContext = context();
    return Optional.ofNullable(
        methodContext.getStore(NAMESPACE)
            .get(methodContext.getUniqueId(), SpendJson[].class)
    );
  }
}
