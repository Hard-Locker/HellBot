package halot.nikitazolin.bot.init.settings.manager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettingsSaver {

  private final Settings settings;

  public void saveToFile(String filePath) {
    saveConfig(settings, filePath);
  }

  private void saveConfig(Settings settings, String filePath) {
    log.info("Update settings with path: " + filePath);
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    Yaml yaml = new Yaml(options);

    try (StringWriter stringWriter = new StringWriter()) {
      yaml.dump(settings, stringWriter);
      String output = stringWriter.toString().replaceAll("^!!.*\n", "");

      try (FileWriter writer = new FileWriter(filePath)) {
        writer.write(output);
      }
    } catch (IOException e) {
      log.error("Error writing the settings file: {}", e);
    }
  }
}
