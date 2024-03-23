package halot.nikitazolin.bot.view;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.util.InputNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsoleMenu {

  private final InputNumber inputNumber;
  
  private static final String NEW_LINE = "\n";
  private static final String DOT_SPACE = ". ";
  
  public void showMenu() {
    log.info("Displaying main menu");

//    dbMenu();
    botManagerMenu();
  }

  private void dbMenu() {
//    log.info("Displaying database refill menu");
//
//    int selectedOption;
//    StringBuilder outputMenu = new StringBuilder();
//
//    outputMenu.append("-----Database manager-----");
//    outputMenu.append(NEW_LINE);
//    outputMenu.append("Do you want to use database?");
//    outputMenu.append(NEW_LINE);
//    outputMenu.append("1. Yes");
//    outputMenu.append(NEW_LINE);
//    outputMenu.append("2. No");
//
//    while (true) {
//      System.out.println(outputMenu);
//      selectedOption = inputNumber.readInputNumber("Select desired action number: ");
//
//      switch (selectedOption) {
//      case 1:
//        System.out.println("Please wait...");
//        
//        //Call database logic
//        return;
//      case 2:
//        return;
//
//      default:
//        System.err.println("Select only the items listed");
//        break;
//      }
//    }
  }

  private void botManagerMenu() {
    log.info("Displaying bot manager menu");
    StringBuilder outputMenu = buildMainMenu();

    while (true) {
      int selectedOption;

      System.out.println(outputMenu);
      selectedOption = inputNumber.readInputNumber("Select desired action number: ");

      switch (selectedOption) {
      case 1:
        //Option 1
        log.debug("option 1");
        break;
      case 2:
        //Option 2
        log.debug("option 2");
        break;
      case 0:
        System.out.println("Goodbye!");
        log.debug("Selected closing application");
        return;

      default:
        System.err.println("Select only the items listed");
        break;
      }
    }
  }

  private StringBuilder buildMainMenu() {
    StringBuilder outputMenu = new StringBuilder();
    Map<Integer, String> menuOption = new LinkedHashMap<>();

    menuOption.put(1, "Option 1");
    menuOption.put(2, "Option 2");
    menuOption.put(0, "Exit");

    outputMenu.append("-----Bot manager-----");
    outputMenu.append(NEW_LINE);

    for (Map.Entry<Integer, String> entry : menuOption.entrySet()) {
      outputMenu.append(entry.getKey());
      outputMenu.append(DOT_SPACE);
      outputMenu.append(entry.getValue());
      outputMenu.append(NEW_LINE);
    }

    return outputMenu;
  }
}
