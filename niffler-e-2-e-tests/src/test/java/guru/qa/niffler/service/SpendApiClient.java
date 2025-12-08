package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

public class SpendApiClient implements SpendClient {
  @Override
  public SpendJson createSpend(SpendJson spend) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public Optional<CategoryJson> findByNameAndUsername(String name, String username) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
