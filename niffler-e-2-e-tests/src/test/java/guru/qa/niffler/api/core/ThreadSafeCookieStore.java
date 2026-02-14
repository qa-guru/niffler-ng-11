package guru.qa.niffler.api.core;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {
  INSTANCE;

  private final ThreadLocal<CookieStore> store = ThreadLocal
      .withInitial(this::inMemoryCookieStore);

  @Override
  public void add(URI uri, HttpCookie cookie) {
    store.get().add(uri, cookie);
  }

  @Override
  public List<HttpCookie> get(URI uri) {
    return store.get().get(uri);
  }

  @Override
  public List<HttpCookie> getCookies() {
    return store.get().getCookies();
  }

  @Override
  public List<URI> getURIs() {
    return store.get().getURIs();
  }

  @Override
  public boolean remove(URI uri, HttpCookie cookie) {
    return store.get().remove(uri, cookie);
  }

  @Override
  public boolean removeAll() {
    return store.get().removeAll();
  }

  private CookieStore inMemoryCookieStore() {
    return new CookieManager().getCookieStore();
  }

  public String cookieValue(String cookieName) {
    return getCookies().stream()
        .filter(c -> c.getName().equals(cookieName))
        .map(HttpCookie::getValue)
        .findFirst()
        .orElseThrow();
  }
}
