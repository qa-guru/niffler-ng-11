package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
  private final ElementsCollection spendings = $$("tbody tr");

  public EditSpendingPage editSpending(String description) {
    spendings.find(text(description))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage checkSpendingDescription(String description) {
    spendings.find(text(description)).should(visible);
    return this;
  }
}
