package halot.nikitazolin.bot.init.authorization.manager;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.init.authorization.model.DatabaseVendor;
import halot.nikitazolin.bot.util.InputNumber;
import halot.nikitazolin.bot.util.InputText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationConsoleMenu {

  private final InputNumber inputNumber;
  private final InputText inputText;
  private final AuthorizationData authorizationData;
  private final AuthorizationSaver authorizationSaver;

  public void showMenu(String filePath) {
    log.info("Displaying menu");

    apiMenu(authorizationData, filePath);
//    youtubeMenu(authorizationData, filePath);
    dbMenu(authorizationData, filePath);
  }

  private void apiMenu(AuthorizationData authorizationData, String filePath) {
    log.info("Displaying API authorization menu");
    System.out.println("-----Discord API-----");
    String apiKey = getStringInput("Enter API token in next line:");

    authorizationData.getDiscordApi().setApiKey(apiKey);

    authorizationSaver.saveToFile(filePath);
  }

  //Now not used
  private void youtubeMenu(AuthorizationData authorizationData, String filePath) {
    log.info("Displaying YouTube authorization menu");
    boolean youtubeEnabled = getTwoOptionInput("-----YouTube authorization-----",
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

    authorizationSaver.saveToFile(filePath);
  }

  private void dbMenu(AuthorizationData authorizationData, String filePath) {
    log.info("Displaying database usage menu");
    boolean dbEnabled = getTwoOptionInput("-----Database manager-----", "Do you want to use database?");
    DatabaseVendor dbVendor = null;
    String dbName = null;
    String dbUrl = null;
    String dbUsername = null;
    String dbPassword = null;

    if (dbEnabled == true) {
      dbVendor = getDbOptionInput();
    }

    if (dbVendor.equals(DatabaseVendor.POSTGRESQL)) {
      dbName = getStringInput("Enter database name in next line:");
      dbUrl = getStringInput("Enter database URL in next line (example: jdbc:postgresql://localhost:5432/dbname):");
      dbUsername = getStringInput("Enter database username in next line:");
      dbPassword = getStringInput("Enter database password in next line:");
    }

    authorizationData.getDatabase().setDbEnabled(dbEnabled);
    authorizationData.getDatabase().setDbVendor(dbVendor);
    authorizationData.getDatabase().setDbName(dbName);
    authorizationData.getDatabase().setDbUrl(dbUrl);
    authorizationData.getDatabase().setDbUsername(dbUsername);
    authorizationData.getDatabase().setDbPassword(dbPassword);

    authorizationSaver.saveToFile(filePath);
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

  private boolean getTwoOptionInput(String optionTitle, String optionDescription) {
    log.debug("Requesting input: {}", optionDescription);
    String newLine = System.lineSeparator();
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

  private DatabaseVendor getDbOptionInput() {
    log.debug("Select database vendor");

    int selectedOption;
    String newLine = System.lineSeparator();
    StringBuilder outputMenu = new StringBuilder();
    outputMenu.append("-----Database selector-----");
    outputMenu.append(newLine);
    outputMenu.append("Which database do you want to use?");
    outputMenu.append(newLine);

    DatabaseVendor[] vendors = DatabaseVendor.values();

    for (int i = 0; i < vendors.length; i++) {
      outputMenu.append(i + 1);
      outputMenu.append(". ");
      outputMenu.append(vendors[i].getName());
      outputMenu.append(newLine);
    }

    System.out.println(outputMenu);

    while (true) {
      selectedOption = inputNumber.readInputNumber("Select desired action number: ");

      if (selectedOption >= 1 && selectedOption <= vendors.length) {
        return vendors[selectedOption - 1];
      } else {
        System.err.println("Select only the items listed");
      }
    }
  }
}
