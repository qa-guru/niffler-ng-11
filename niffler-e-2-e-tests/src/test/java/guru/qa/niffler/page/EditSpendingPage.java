package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitButton = $("#save");

  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  public MainPage save() {
    submitButton.click();
    return new MainPage();
  }
}
