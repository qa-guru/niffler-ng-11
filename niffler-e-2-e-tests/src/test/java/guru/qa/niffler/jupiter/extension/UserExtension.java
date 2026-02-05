package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  public static final String DEFAULT_PASSWORD = "12345";

  private final UserClient userClient = new UserDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {

              if ("".equals(userAnno.username())) {
                final String username = randomUsername();

                final UserJson user = userClient.createUser(username, DEFAULT_PASSWORD);
                final List<UserJson> incomes = userClient.addIncomeInvitation(user, userAnno.incomeInvitations());
                final List<UserJson> outcomes = userClient.addOutcomeInvitation(user, userAnno.outcomeInvitations());
                final List<UserJson> friends = userClient.addFriend(user, userAnno.friends());

                context.getStore(NAMESPACE).put(
                    context.getUniqueId(),
                    user.addTestData(
                        new TestData(
                            DEFAULT_PASSWORD,
                            incomes,
                            outcomes,
                            friends,
                            new ArrayList<>(),
                            new ArrayList<>()
                        )
                    )
                );
              }
            }
        );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext,
                                    ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdUser().orElseThrow();
  }

  public static Optional<UserJson> createdUser() {
    final ExtensionContext methodContext = context();
    return Optional.ofNullable(
        methodContext.getStore(NAMESPACE)
        .get(methodContext.getUniqueId(), UserJson.class)
    );
  }
}
