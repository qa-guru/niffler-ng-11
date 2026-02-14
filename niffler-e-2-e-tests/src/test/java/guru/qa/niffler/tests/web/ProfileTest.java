package guru.qa.niffler.tests.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

  private Config cfg;

  @User(
      categories = @Category(
          archived = true
      )
  )
  @Test
  void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
    final CategoryJson expectedCategory = user.testData().categories().getFirst();

    Selenide.open(cfg.frontUrl(), LoginPage.class)
        .successLogin(user.username(), user.testData().password())
        .checkThatPageLoaded();

    Selenide.open(cfg.frontUrl() + "profile", ProfilePage.class)
        .checkArchivedCategoryExists(expectedCategory.name());
  }

  @User(
      categories = @Category(
          archived = false
      )
  )
  @Test
  void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
    Selenide.open(cfg.frontUrl(), LoginPage.class)
        .successLogin("duck", "12345")
        .checkThatPageLoaded();

    Selenide.open(cfg.frontUrl() + "profile", ProfilePage.class)
        .checkCategoryExists(category.name());
  }
}
