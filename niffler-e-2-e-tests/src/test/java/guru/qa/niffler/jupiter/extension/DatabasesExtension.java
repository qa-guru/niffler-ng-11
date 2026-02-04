package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.tpl.Connections;

public class DatabasesExtension implements SuiteExtension {
  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
  }
}
