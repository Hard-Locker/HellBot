package halot.nikitazolin.bot.view;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.util.InputNumber;
import halot.nikitazolin.bot.util.InputText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsoleMenu {

  private final InputNumber inputNumber;
  private final InputText inputText;

  private static final String NEW_LINE = "\n";

  public void showMenu(String secretFilePath) {
    log.info("Displaying menu");

    apiMenu(secretFilePath);
    youtubeMenu(secretFilePath);
    dbMenu(secretFilePath);
  }

  private void apiMenu(String secretFilePath) {
    log.info("Displaying API authorization menu");
    System.out.println("-----Discord API-----");
    String apiKey = getStringInput("Enter API token in next line:");
    saveApiKey(apiKey, secretFilePath);
  }

  private void youtubeMenu(String secretFilePath) {
    log.info("Displaying YouTube authorization menu");

    boolean youtubeAuthorization = getUsageOptionInput("-----YouTube authorization-----",
        "Do you want to log in to your YouTube profile?");
    String youtubeLogin = null;
    String youtubePassword = null;

    if (youtubeAuthorization == true) {
      youtubeLogin = getStringInput("Enter YouTube login in next line:");
      youtubePassword = getStringInput("Enter YouTube password in next line:");
    }

    saveYoutubeInfo(youtubeAuthorization, youtubeLogin, youtubePassword, secretFilePath);
  }

  private void dbMenu(String secretFilePath) {
    log.info("Displaying database usage menu");

    boolean databaseUse = getUsageOptionInput("-----Database manager-----", "Do you want to use database?");
    String dbName = null;
    String dbUrl = null;
    String dbUsername = null;
    String dbPassword = null;

    if (databaseUse == true) {
      dbName = getStringInput("Enter database name in next line:");
      dbUrl = getStringInput("Enter database URL in next line (example: jdbc:postgresql://localhost:5432/dbname):");
      dbUsername = getStringInput("Enter database username in next line:");
      dbPassword = getStringInput("Enter database password in next line:");
    }

    saveDbInfo(databaseUse, dbName, dbUrl, dbUsername, dbPassword, secretFilePath);
  }

  private String getStringInput(String inputDescription) {
    String userInput = null;

    do {
      log.debug("Requesting input: {}", inputDescription);
      StringBuilder input = inputText.readInputString(inputDescription);

      if (input.isEmpty()) {
        System.out.println("You can't skip this input");
      } else {
        userInput = input.toString().trim();
      }

    } while (userInput == null);

    return userInput;
  }

  private boolean getUsageOptionInput(String optionTitle, String optionDescription) {
    log.debug("Requesting input: {}", optionDescription);
    int selectedOption;
    StringBuilder outputMenu = new StringBuilder();
    outputMenu.append(optionTitle);
    outputMenu.append(NEW_LINE);
    outputMenu.append(optionDescription);
    outputMenu.append(NEW_LINE);
    outputMenu.append("1. Yes");
    outputMenu.append(NEW_LINE);
    outputMenu.append("2. No");

    System.out.println(outputMenu);

    while (true) {
      selectedOption = inputNumber.readInputNumber("Select desired action number: ");

      switch (selectedOption) {
      case 1:
        return true;
      case 2:
        return false;
      default:
        System.err.println("Select only the items listed");
        break;
      }
    }
  }

  private void saveApiKey(String apiKey, String secretFilePath) {
    log.info("Save API key");
    Yaml yaml = new Yaml();
    Map<String, Object> data;

    try (FileInputStream fis = new FileInputStream(secretFilePath)) {
      data = yaml.load(fis);
    } catch (IOException e) {
      log.error("Error reading the secrets file: {}", e);
      return;
    }

    data.put("apiKey", apiKey);

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    yaml = new Yaml(options);

    try (FileWriter writer = new FileWriter(secretFilePath)) {
      yaml.dump(data, writer);
    } catch (IOException e) {
      log.error("Error writing the secrets file: {}", e);
    }
  }

  private void saveYoutubeInfo(boolean youtubeAuthorization, String youtubeLogin, String youtubePassword,
      String secretFilePath) {
    Yaml yaml = new Yaml();
    Map<String, Object> data;

    try (FileInputStream fis = new FileInputStream(secretFilePath)) {
      data = yaml.load(fis);
    } catch (IOException e) {
      log.error("Error reading the secrets file: {}", e);
      return;
    }

    data.put("youtubeAuthorization", youtubeAuthorization);
    data.put("youtubeLogin", youtubeLogin);
    data.put("youtubePassword", youtubePassword);

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    yaml = new Yaml(options);

    try (FileWriter writer = new FileWriter(secretFilePath)) {
      yaml.dump(data, writer);
    } catch (IOException e) {
      log.error("Error writing the secrets file: {}", e);
    }
  }

  private void saveDbInfo(boolean databaseUse, String dbName, String dbUrl, String dbUsername, String dbPassword,
      String secretFilePath) {
    Yaml yaml = new Yaml();
    Map<String, Object> data;

    try (FileInputStream fis = new FileInputStream(secretFilePath)) {
      data = yaml.load(fis);
    } catch (IOException e) {
      log.error("Error reading the secrets file: {}", e);
      return;
    }

    data.put("databaseUse", databaseUse);
    data.put("dbName", dbName);
    data.put("dbUrl", dbUrl);
    data.put("dbUsername", dbUsername);
    data.put("dbPassword", dbPassword);

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    yaml = new Yaml(options);

    try (FileWriter writer = new FileWriter(secretFilePath)) {
      yaml.dump(data, writer);
    } catch (IOException e) {
      log.error("Error writing the secrets file: {}", e);
    }
  }
}
