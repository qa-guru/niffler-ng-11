package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

  private final SelenideElement alert = $(".MuiSnackbar-root");

  @SuppressWarnings("unchecked")
  public T checkAlert(String alert) {
    this.alert.shouldHave(text(alert));
    return (T) this;
  }
}
