package halot.nikitazolin.bot.init.authorization;

import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationLoader {

  private final AuthorizationData authorizationData;

  public void load(String filePath) {
    TypeDescription authDataTypeDesc = new TypeDescription(AuthorizationData.class);
    Constructor constructor = new Constructor(AuthorizationData.class, new LoaderOptions());
    constructor.addTypeDescription(authDataTypeDesc);
    Yaml yaml = new Yaml(constructor);

    try (InputStream inputStream = new FileInputStream(filePath)) {
      AuthorizationData loadedData = yaml.load(inputStream);

      authorizationData.setDiscordApi(loadedData.getDiscordApi());
      authorizationData.setYoutube(loadedData.getYoutube());
      authorizationData.setDatabase(loadedData.getDatabase());

      log.info("Authorization data loaded successfully from {}", filePath);
    } catch (Exception e) {
      log.error("Unable to load authorization data from {}: {}", filePath, e.getMessage());
    }
  }
}
