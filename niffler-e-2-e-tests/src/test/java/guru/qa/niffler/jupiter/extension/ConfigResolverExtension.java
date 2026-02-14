package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.config.Config;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class ConfigResolverExtension implements TestInstancePostProcessor {
  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
    for (Field field : testInstance.getClass().getDeclaredFields()) {
      if (field.getType().isAssignableFrom(Config.class)) {
        field.setAccessible(true);
        field.set(testInstance, Config.getInstance());
      }
    }
  }
}
