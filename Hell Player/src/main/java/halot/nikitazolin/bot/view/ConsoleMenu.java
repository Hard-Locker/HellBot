package halot.nikitazolin.bot.view;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.init.AuthorizationData;
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
  private final AuthorizationData authorizationData;

  public void showMenu(String filePath) {
    log.info("Displaying menu");

    apiMenu(authorizationData, filePath);
    youtubeMenu(authorizationData, filePath);
    dbMenu(authorizationData, filePath);
  }

  private void apiMenu(AuthorizationData authorizationData, String filePath) {
    log.info("Displaying API authorization menu");
    System.out.println("-----Discord API-----");
    String apiKey = getStringInput("Enter API token in next line:");

    authorizationData.getDiscordApi().setApiKey(apiKey);
    saveConfig(authorizationData, filePath);
  }

  private void youtubeMenu(AuthorizationData authorizationData, String filePath) {
    log.info("Displaying YouTube authorization menu");
    boolean youtubeEnabled = getUsageOptionInput("-----YouTube authorization-----",
        "Do you want log in to YouTube profile?");
    String youtubeLogin = null;
    String youtubePassword = null;

    if (youtubeEnabled == true) {
      youtubeLogin = getStringInput("Enter YouTube login in next line:");
      youtubePassword = getStringInput("Enter YouTube password in next line:");
    }

    authorizationData.getYoutube().setYoutubeEnabled(youtubeEnabled);
    authorizationData.getYoutube().setYoutubeLogin(youtubeLogin);
    authorizationData.getYoutube().setYoutubePassword(youtubePassword);
    saveConfig(authorizationData, filePath);
  }

  private void dbMenu(AuthorizationData authorizationData, String filePath) {
    log.info("Displaying database usage menu");
    boolean dbEnabled = getUsageOptionInput("-----Database manager-----", "Do you want to use database?");
    String dbName = null;
    String dbUrl = null;
    String dbUsername = null;
    String dbPassword = null;

    if (dbEnabled == true) {
      dbName = getStringInput("Enter database name in next line:");
      dbUrl = getStringInput("Enter database URL in next line (example: jdbc:postgresql://localhost:5432/dbname):");
      dbUsername = getStringInput("Enter database username in next line:");
      dbPassword = getStringInput("Enter database password in next line:");
    }

    authorizationData.getDatabase().setDbEnabled(dbEnabled);
    authorizationData.getDatabase().setDbName(dbName);
    authorizationData.getDatabase().setDbUrl(dbUrl);
    authorizationData.getDatabase().setDbUsername(dbUsername);
    authorizationData.getDatabase().setDbPassword(dbPassword);
    saveConfig(authorizationData, filePath);
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
    String newLine = "\n";
    int selectedOption;
    StringBuilder outputMenu = new StringBuilder();
    outputMenu.append(optionTitle);
    outputMenu.append(newLine);
    outputMenu.append(optionDescription);
    outputMenu.append(newLine);
    outputMenu.append("1. Yes");
    outputMenu.append(newLine);
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

  private void saveConfig(AuthorizationData authorizationData, String filePath) {
    log.info("Update config with path: " + filePath);
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    Yaml yaml = new Yaml(options);

    try (FileWriter writer = new FileWriter(filePath)) {
      yaml.dump(authorizationData, writer);
    } catch (IOException e) {
      log.error("Error writing the secrets file: {}", e);
    }
  }
}
