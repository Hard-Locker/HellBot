package halot.nikitazolin.bot.init.authorization.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationFileChecker {

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
        if (apiKeyInFile.contains("apiKey:")) {
          String apiKeyValue = apiKeyInFile.substring("apiKey:".length()).trim();

          return (apiKeyValue.length() > 50);
        }
      }
    } catch (IOException e) {
      log.error("Error reading structure to path: " + filePath);
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
      log.error("Error creating file in path: ", filePath);

      return false;
    }
  }

  private boolean writeInitialStructure(String filePath) {
    File file = new File(filePath);

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      String newLine = System.lineSeparator();
      String initialContent = "database:" + newLine
          + "  dbEnabled: false" + newLine
          + "  dbName: null" + newLine
          + "  dbPassword: null" + newLine
          + "  dbUrl: null" + newLine
          + "  dbUsername: null" + newLine
          + "  dbVendor: null" + newLine
          + "discordApi:" + newLine
          + "  apiKey: null" + newLine
          + "youtube:" + newLine
          + "  youtubeEnabled: false" + newLine
          + "  youtubeLogin: null" + newLine
          + "  youtubePassword: null";

      writer.write(initialContent);
      log.debug("Write initial structure to path: " + filePath);

      return true;
    } catch (IOException e) {
      log.error("Error writing structure to path: " + filePath);

      return false;
    }
  }
}
