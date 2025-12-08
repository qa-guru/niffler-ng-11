package guru.qa.niffler.grpc;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.repository.CurrencyRepository;
import guru.qa.niffler.service.BaseGrpcTest;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
    "grpc.server.port=0",
    "grpc.server.inProcessName=test",
    "grpc.server.shutdownGracePeriod=0"
})
@DirtiesContext
@DisplayName("Integration tests for gRPC Currency Service")
class GrpcCurrencyServiceIntegrationTest extends BaseGrpcTest {

  private static Channel channel;
  private static NifflerCurrencyServiceGrpc.NifflerCurrencyServiceBlockingStub blockingStub;

  @Autowired
  private CurrencyRepository currencyRepository;

  @BeforeAll
  static void beforeAll() {
    // Use in-process server for testing
    channel = ManagedChannelBuilder.forAddress("localhost", 0)
        .usePlaintext()
        .build();
    blockingStub = NifflerCurrencyServiceGrpc.newBlockingStub(channel);
  }

  @AfterAll
  static void afterAll() {
    ((io.grpc.ManagedChannel) channel).shutdownNow();
  }

  @Test
  @DisplayName("Should return all available currencies")
  void shouldReturnAllCurrencies() {
    CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());

    assertNotNull(response);
    List<Currency> currencies = response.getAllCurrenciesList();
    assertEquals(4, currencies.size());
    assertTrue(currencies.stream().anyMatch(c -> c.getCurrency() == CurrencyValues.RUB));
    assertTrue(currencies.stream().anyMatch(c -> c.getCurrency() == CurrencyValues.USD));
    assertTrue(currencies.stream().anyMatch(c -> c.getCurrency() == CurrencyValues.EUR));
    assertTrue(currencies.stream().anyMatch(c -> c.getCurrency() == CurrencyValues.KZT));
  }

  @Test
  @DisplayName("Should calculate currency conversion correctly")
  void shouldCalculateCurrencyConversion() {
    // Test RUB to USD conversion
    CalculateRequest request = CalculateRequest.newBuilder()
        .setAmount(1500.0)
        .setSpendCurrency(CurrencyValues.RUB)
        .setDesiredCurrency(CurrencyValues.USD)
        .build();

    CalculateResponse response = blockingStub.calculateRate(request);

    // 1500 RUB * 0.015 (RUB rate) / 1.0 (USD rate) = 22.5 USD
    assertEquals(22.5, response.getCalculatedAmount(), 0.001);
  }

  @Test
  @DisplayName("Should return same amount when converting to the same currency")
  void shouldReturnSameAmountForSameCurrency() {
    double amount = 100.0;

    CalculateRequest request = CalculateRequest.newBuilder()
        .setAmount(amount)
        .setSpendCurrency(CurrencyValues.EUR)
        .setDesiredCurrency(CurrencyValues.EUR)
        .build();

    CalculateResponse response = blockingStub.calculateRate(request);

    assertEquals(amount, response.getCalculatedAmount(), 0.001);
  }

  @Test
  @DisplayName("Should handle zero amount conversion")
  void shouldHandleZeroAmount() {
    CalculateRequest request = CalculateRequest.newBuilder()
        .setAmount(0.0)
        .setSpendCurrency(CurrencyValues.USD)
        .setDesiredCurrency(CurrencyValues.EUR)
        .build();

    CalculateResponse response = blockingStub.calculateRate(request);

    assertEquals(0.0, response.getCalculatedAmount(), 0.001);
  }

  @Test
  @DisplayName("Should throw exception for invalid currency")
  void shouldThrowExceptionForInvalidCurrency() {
    CalculateRequest request = CalculateRequest.newBuilder()
        .setAmount(100.0)
        .setSpendCurrency(CurrencyValues.UNRECOGNIZED)
        .setDesiredCurrency(CurrencyValues.USD)
        .build();

    assertThrows(StatusRuntimeException.class, () -> blockingStub.calculateRate(request));
  }
}
