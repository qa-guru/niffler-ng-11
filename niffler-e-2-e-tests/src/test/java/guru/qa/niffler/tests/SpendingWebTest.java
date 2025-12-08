package guru.qa.niffler.tests;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.Spend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

public class SpendingWebTest {

  private static final Config CFG = Config.getInstance();

  @Test
  @Spend(
      username = "duck",
      description = "Отпуск в Европе",
      amount = 1000,
      currency = CurrencyValues.EUR,
      category = "Кафе"
  )
  void spendingDescriptionShouldBeChangedAfterTableAction(SpendJson spendJson) {
    final String newDescription = "Отпуск в России";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .login("duck", "12345")
        .editSpending(spendJson.description())
        .editDescription(newDescription)
        .save()
        .checkSpendingDescription(newDescription);
  }
}
