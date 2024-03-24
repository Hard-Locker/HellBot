package halot.nikitazolin.bot.util;

import java.io.InputStream;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
public class YamlLoader {

  private Map<String, Object> config;

  public void loadConfig(String configFile) {
    Yaml yaml = new Yaml();
    
    try (InputStream in = getClass().getClassLoader().getResourceAsStream(configFile)) {
      config = yaml.load(in);
    } catch (Exception e) {
      log.error("Cannot load configuration file" + e);
    }
  }
}
