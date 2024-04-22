package halot.nikitazolin.bot.init.settings.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SettingsFileChecker {

  public boolean ensureFileExists(String filePath) {
    if (checkFileExists(filePath)) {
      return true;
    } else {
      createFile(filePath);
      writeInitialStructure(filePath);

      return false;
    }
  }

  private boolean checkFileExists(String filePath) {
    File file = new File(filePath);

    return file.exists();
  }

  private boolean createFile(String filePath) {
    try {
      File file = new File(filePath);
      file.createNewFile();
      log.debug("Create file with path: {}", filePath);

      return true;
    } catch (IOException e) {
      log.error("Error creating file in path: {}", filePath);

      return false;
    }
  }

  private boolean writeInitialStructure(String filePath) {
    Settings settings = new Settings();
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

      log.debug("Wrote initial structure to file with path: {}", filePath);

      return true;
    } catch (IOException e) {
      log.error("Error writing the file with path: {}", filePath);

      return false;
    }
  }
}
