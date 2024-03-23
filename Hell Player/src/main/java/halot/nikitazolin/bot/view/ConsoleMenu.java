package halot.nikitazolin.bot.view;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import halot.nikitazolin.bot.util.InputNumber;
import halot.nikitazolin.bot.util.InputText;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope("prototype")
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

    String apiKey = getApiKeyInput();
    saveApiKey(apiKey, secretFilePath);
  }
  
  private String getApiKeyInput() {
    String apiKey = null;

    do {
      log.debug("Requesting API key input");
      StringBuilder input = inputText.readInputString("Enter API token in next line");

      if (input.isEmpty()) {
        System.out.println("You can't skip API input");
      } else {
        apiKey = input.toString();
      }
    } while (apiKey == null);
    
    return apiKey;
  }
  
  private void saveApiKey(String apiKey, String secretFilePath) {
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

  private void youtubeMenu(String secretFilePath) {
    log.info("Displaying API authorization menu");
    System.out.println("-----YouTube authorization-----");

    
  }

  //TODO
  private void dbMenu(String secretFilePath) {
    log.info("Displaying database usage menu");

    int selectedOption;
    StringBuilder outputMenu = new StringBuilder();
    outputMenu.append("-----Database manager-----");
    outputMenu.append(NEW_LINE);
    outputMenu.append("Do you want to use database?");
    outputMenu.append(NEW_LINE);
    outputMenu.append("1. Yes");
    outputMenu.append(NEW_LINE);
    outputMenu.append("2. No");

    while (true) {
      System.out.println(outputMenu);
      selectedOption = inputNumber.readInputNumber("Select desired action number: ");

      switch (selectedOption) {
      case 1:
        System.out.println("Please wait...");

        // Call database logic
        return;
      case 2:
        return;

      default:
        System.err.println("Select only the items listed");
        break;
      }
    }
  }
}
