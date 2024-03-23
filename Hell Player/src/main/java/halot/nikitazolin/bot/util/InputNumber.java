package halot.nikitazolin.bot.util;

import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class InputNumber {

  public int readInputNumber(String inputDescription) {
    @SuppressWarnings("resource")
    Scanner input = new Scanner(System.in);
    int selectedOption;

    if (inputDescription == null) {
      throw new NullPointerException("Missing title phrase");
    }

    System.out.println(inputDescription);

    while (true) {
      try {
        selectedOption = Integer.parseInt(input.nextLine());

        break;
      } catch (NumberFormatException error) {
        System.err.println("Enter only integer digits");
        System.out.println("Try again " + inputDescription);
      }
    }

    return Math.abs(selectedOption);
  }
}
