package guru.qa.niffler.tests.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {

  private static final Config CFG = Config.getInstance();

  @User(
      friends = 1
  )
  @Test
  void friendShouldBePresentInFriendsTable(UserJson user) {
    final UserJson expectedFriend = user.testData().friends().getFirst();

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), user.testData().password())
        .checkThatPageLoaded()
        .friendsPage()
        .checkExistingFriends(expectedFriend.username());
  }

  @User
  @Test
  void friendsTableShouldBeEmptyForNewUser(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), user.testData().password())
        .checkThatPageLoaded()
        .friendsPage()
        .checkNoExistingFriends();
  }

  @User(
      incomeInvitations = 1
  )
  @Test
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    final UserJson expectedIncome = user.testData().incomeInvitations().getFirst();

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), user.testData().password())
        .checkThatPageLoaded()
        .friendsPage()
        .checkExistingInvitations(expectedIncome.username());
  }

  @User(
      outcomeInvitations = 1
  )
  @Test
  void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
    final UserJson expectedOutcome = user.testData().outcomeInvitations().getFirst();

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), user.testData().password())
        .checkThatPageLoaded()
        .allPeoplesPage()
        .checkInvitationSentToUser(expectedOutcome.username());
  }
}
