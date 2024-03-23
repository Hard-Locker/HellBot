package halot.nikitazolin.bot.util;

import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class InputText {

  public StringBuilder readInputString(String inputDescription) {
    @SuppressWarnings("resource")
    Scanner input = new Scanner(System.in);
    
    if (inputDescription == null) {
      throw new NullPointerException("Missing title phrase");
    }
    
    System.out.println(inputDescription);
    StringBuilder phrase = new StringBuilder(input.nextLine());
    return phrase;
  }
}
