package halot.nikitazolin.bot.init;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthorizationChecker {

  public boolean ensureFileExists(String filePath) {
    if (checkFileExists(filePath) && checkApiKey(filePath)) {
      return true;
    } else {
      createFile(filePath);
      writeInitialStructure(filePath);

      return false;
    }
  }

  private boolean checkApiKey(String filePath) {
    File file = new File(filePath);
    String apiKeyInFile;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      while ((apiKeyInFile = reader.readLine()) != null) {
        if (apiKeyInFile.startsWith("apiKey:")) {
          String apiKeyValue = apiKeyInFile.substring("apiKey:".length()).trim();

          return !apiKeyValue.isEmpty();
        }
      }
    } catch (IOException e) {
      log.error("Error reading the secrets file: ", e);
    }

    return false;
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
      log.error("Error creating secrets file: ", e);

      return false;
    }
  }

  private boolean writeInitialStructure(String filePath) {
    File file = new File(filePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      String newLine = System.lineSeparator();
      String initialContent = "discordApi:" + newLine
          + "  apiKey: " + newLine
          + "youtube:" + newLine
          + "  youtubeEnabled: false" + newLine
          + "  youtubeLogin: " + newLine
          + "  youtubePassword: " + newLine
          + "database:" + newLine
          + "  dbEnabled: false" + newLine
          + "  dbName: " + newLine
          + "  dbUrl: " + newLine
          + "  dbUsername: " + newLine
          + "  dbPassword: ";

      writer.write(initialContent);
      log.debug("Write initial structure to path: " + filePath);

      return true;
    } catch (IOException e) {
      log.error("Error with writing structure secrets.yml. Error: " + e);

      return false;
    }
  }
}
