package halot.nikitazolin.bot.init.authorization.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
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

          return (apiKeyValue.length() > 10);
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
    AuthorizationData authorizationData = new AuthorizationData();
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

      log.debug("Wrote initial structure to file with path: {}", filePath);

      return true;
    } catch (IOException e) {
      log.error("Error writing the secrets file: {}", e);

      return false;
    }
  }
}
