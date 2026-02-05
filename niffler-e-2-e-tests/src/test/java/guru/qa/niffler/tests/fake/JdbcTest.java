package guru.qa.niffler.tests.fake;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.Optional;

@Disabled
public class JdbcTest {

  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "cat-name-tx-3",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-tx-3",
            "duck"
        )
    );

    System.out.println(spend);
  }


  static UserDbClient usersDbClient = new UserDbClient();

  @ValueSource(strings = {
      "valentin-14"
  })
  @ParameterizedTest
  void springJdbcTest(String uname) {
    UserJson user = usersDbClient.createUser(
        uname,
        "12345"
    );
    Assertions.assertNotNull(user);
  }

  @Test
  void extractorTest() {
    AuthUserRepositorySpringJdbc authUserRepositorySpringJdbc = new AuthUserRepositorySpringJdbc();
    Optional<AuthUserEntity> username = authUserRepositorySpringJdbc.findByUsername("valentin-11");
    Assertions.assertEquals(2, username.get().getAuthorities().size());
  }
}
