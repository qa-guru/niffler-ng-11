package guru.qa.niffler.config;

public enum LocalConfig implements Config {
  INSTANCE;


  @Override
  public String frontUrl() {
    return "http://localhost:3000";
  }

  @Override
  public String spendJdbcUrl() {
    return "jdbc:postgresql://localhost:5432/niffler-spend";
  }

  @Override
  public String dbUsername() {
    return "postgres";
  }

  @Override
  public String dbPassword() {
    return "secret";
  }
}
