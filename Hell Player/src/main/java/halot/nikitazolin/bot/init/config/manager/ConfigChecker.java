package halot.nikitazolin.bot.init.config.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConfigChecker {

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
      log.debug("Create file: " + filePath);

      return true;
    } catch (IOException e) {
      log.error("Error creating file in path: ", filePath);

      return false;
    }
  }

  private boolean writeInitialStructure(String filePath) {
    File file = new File(filePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      String newLine = System.lineSeparator();
      String initialContent = "prefix: " + newLine
          + "songInStatus: ";

      writer.write(initialContent);
      log.debug("Write initial structure to path: " + filePath);

      return true;
    } catch (IOException e) {
      log.error("Error writing structure to path: " + filePath);

      return false;
    }
  }
}
