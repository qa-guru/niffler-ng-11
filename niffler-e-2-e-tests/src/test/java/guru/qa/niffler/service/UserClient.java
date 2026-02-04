package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UserClient {
  UserJson createUserSpringJdbc(UserJson user);

  UserJson createUser(UserJson user);
}
