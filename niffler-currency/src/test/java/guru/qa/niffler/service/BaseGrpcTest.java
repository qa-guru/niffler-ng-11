package guru.qa.niffler.service;

import guru.qa.niffler.data.CurrencyEntity;
import guru.qa.niffler.data.CurrencyValues;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class BaseGrpcTest {

  @Container
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.2")
      .withDatabaseName("niffler-currency")
      .withUsername("postgres")
      .withPassword("secret");

  @Autowired
  protected CurrencyRepository currencyRepository;

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeEach
  void setup() {
    // Clear and initialize test data
    currencyRepository.deleteAll();

    List<CurrencyEntity> testCurrencies = List.of(
        createCurrency(CurrencyValues.RUB, 0.015),
        createCurrency(CurrencyValues.USD, 1.0),
        createCurrency(CurrencyValues.EUR, 1.08),
        createCurrency(CurrencyValues.KZT, 0.0021)
    );

    currencyRepository.saveAll(testCurrencies);
  }

  private CurrencyEntity createCurrency(CurrencyValues currency, double rate) {
    CurrencyEntity entity = new CurrencyEntity();
    entity.setCurrency(currency);
    entity.setCurrencyRate(rate);
    return entity;
  }
}
