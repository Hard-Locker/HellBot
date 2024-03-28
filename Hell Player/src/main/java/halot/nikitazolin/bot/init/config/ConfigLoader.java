package halot.nikitazolin.bot.init.config;

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
public class ConfigLoader {
  
  private final Config config;
  
  public void load(String filePath) {
//    TypeDescription configDataTypeDesc = new TypeDescription(Config.class);
//    Constructor constructor = new Constructor(Config.class, new LoaderOptions());
//    constructor.addTypeDescription(configDataTypeDesc);
//    Yaml yaml = new Yaml(constructor);
//
//    try (InputStream inputStream = new FileInputStream(filePath)) {
//      Config config = yaml.load(inputStream);
//
////      authorizationData.setDiscordApi(loadedData.getDiscordApi());
//
//      log.info("Config data loaded successfully from {}", filePath);
//    } catch (Exception e) {
//      log.error("Unable to load config data from {}: {}", filePath, e.getMessage());
//    }
  }
}
