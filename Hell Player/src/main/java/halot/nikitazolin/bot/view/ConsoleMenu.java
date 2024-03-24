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

  private static final String NEW_LINE = "\n";

  public void showMenu(String secretFilePath) {
    log.info("Displaying menu");

    apiMenu(authorizationData, secretFilePath);
    youtubeMenu(authorizationData, secretFilePath);
    dbMenu(authorizationData, secretFilePath);
  }

  private void apiMenu(AuthorizationData authorizationData, String secretFilePath) {
    log.info("Displaying API authorization menu");
    System.out.println("-----Discord API-----");
    String apiKey = getStringInput("Enter API token in next line:");

    authorizationData.setApiKey(apiKey);
    saveConfig(authorizationData, secretFilePath);
  }

  private void youtubeMenu(AuthorizationData authorizationData, String secretFilePath) {
    log.info("Displaying YouTube authorization menu");

    boolean youtubeAuthorization = getUsageOptionInput("-----YouTube authorization-----",
        "Do you want to log in to your YouTube profile?");
    String youtubeLogin = null;
    String youtubePassword = null;

    if (youtubeAuthorization == true) {
      youtubeLogin = getStringInput("Enter YouTube login in next line:");
      youtubePassword = getStringInput("Enter YouTube password in next line:");
    }

    authorizationData.getYoutubeAuthorization().setEnabled(youtubeAuthorization);
    authorizationData.getYoutubeAuthorization().setLogin(youtubeLogin);
    authorizationData.getYoutubeAuthorization().setPassword(youtubePassword);
    saveConfig(authorizationData, secretFilePath);
  }

  private void dbMenu(AuthorizationData authorizationData, String secretFilePath) {
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

    authorizationData.getDatabaseUse().setEnabled(databaseUse);
    authorizationData.getDatabaseUse().setDbName(dbName);
    authorizationData.getDatabaseUse().setDbUrl(dbUrl);
    authorizationData.getDatabaseUse().setDbUsername(dbUsername);
    authorizationData.getDatabaseUse().setDbPassword(dbPassword);
    saveConfig(authorizationData, secretFilePath);
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

  private void saveConfig(AuthorizationData authorizationData, String secretFilePath) {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    Yaml yaml = new Yaml(options);

    try (FileWriter writer = new FileWriter(secretFilePath)) {
      yaml.dump(authorizationData, writer);
    } catch (IOException e) {
      log.error("Error writing the secrets file: {}", e);
    }
  }
}
