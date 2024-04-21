package halot.nikitazolin.bot.init.authorization.manager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationSaver {

  private final AuthorizationData authorizationData;

  public void saveToFile(String filePath) {
    saveConfig(authorizationData, filePath);
  }

  private void saveConfig(AuthorizationData authorizationData, String filePath) {
    log.info("Update config with path: " + filePath);
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    Yaml yaml = new Yaml(options);

    try (StringWriter stringWriter = new StringWriter()) {
      yaml.dump(authorizationData, stringWriter);
      String output = stringWriter.toString().replaceAll("^!!.*\n", "");

      try (FileWriter writer = new FileWriter(filePath)) {
        writer.write(output);
      }
    } catch (IOException e) {
      log.error("Error writing the secrets file: {}", e);
    }
  }
}
