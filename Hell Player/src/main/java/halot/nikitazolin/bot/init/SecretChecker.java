package halot.nikitazolin.bot.init;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecretChecker {

  public boolean ensureSecretExists(String secretFilePath) {
    if (checkFileExists(secretFilePath)) {
      return true;
    } else {
      createSecretFile(secretFilePath);
      writeInitialStructure(secretFilePath);
      
      return false;
    }
  }

  private boolean checkFileExists(String secretFilePath) {
    File file = new File(secretFilePath);

    return file.exists();
  }

  private boolean createSecretFile(String secretFilePath) {
    try {
      File file = new File(secretFilePath);
      file.createNewFile();
      log.debug("Create secrets.yml");

      return true;
    } catch (IOException e) {
      log.error("Error creating secrets file: ", e);

      return false;
    }
  }

  private boolean writeInitialStructure(String secretFilePath) {
    File file = new File(secretFilePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      String newLine = "\n";
      String initialContent = "apiKey: "
          + newLine
          + newLine
          + "youtubeAuthorization: "
          + newLine
          + "  youtubeLogin: "
          + newLine
          + "  youtubePassword: "
          + newLine
          + newLine
          + "databaseUse: "
          + newLine
          + "  dbName: "
          + newLine
          + "  dbUrl: "
          + newLine
          + "  dbUsername: "
          + newLine
          + "  dbPassword: ";

      writer.write(initialContent);
      log.debug("Write initial structure to secrets.yml");

      return true;
    } catch (IOException e) {
      log.error("Error with writing structure secrets.yml. Error: " + e);

      return false;
    }
  }
}
