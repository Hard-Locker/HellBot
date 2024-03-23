package halot.nikitazolin.bot.init;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConfigChecker {

  public boolean ensureConfigExists(String configFilePath) {
    if (checkFileExists(configFilePath)) {
      return true;
    } else {
      createFile(configFilePath);
      writeInitialStructure(configFilePath);

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
      log.debug("Create config.yml");

      return true;
    } catch (IOException e) {
      log.error("Error creating configs file: ", e);

      return false;
    }
  }

  private boolean writeInitialStructure(String filePath) {
    File file = new File(filePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      String newLine = "\n";
      String initialContent = "apiKey: " + newLine + "youtubeAuthorization: ";

      writer.write(initialContent);
      log.debug("Write initial structure to config.yml");

      return true;
    } catch (IOException e) {
      log.error("Error with writing structure config.yml. Error: " + e);

      return false;
    }
  }
}
