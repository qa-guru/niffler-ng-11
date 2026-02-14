package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import lombok.SneakyThrows;

import static java.util.Objects.requireNonNull;

public final class GhApiClient extends RestClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private final GhApi ghApi;

  public GhApiClient() {
    super(CFG.githubUrl());
    this.ghApi = create(GhApi.class);
  }

  @SneakyThrows
  public String issueState(String issueNumber) {
    JsonNode responseBody = ghApi.issue(
        "Bearer " + System.getenv(GH_TOKEN_ENV),
        issueNumber
    ).execute().body();
    return requireNonNull(responseBody).get("state").asText();
  }
}
