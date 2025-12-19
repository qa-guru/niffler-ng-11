package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");

  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.setValue(description);
    return this;
  }

  public MainPage save() {
    submitBtn.click();
    return new MainPage();
  }
}
