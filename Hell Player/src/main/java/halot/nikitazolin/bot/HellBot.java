package halot.nikitazolin.bot;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import halot.nikitazolin.bot.command.manager.CommandRegistrator;

@SpringBootApplication
public class HellBot {

  private static JdaService jdaService;
  private static CommandRegistrator commandRegistry;

  public static void main(String[] args) {
    jdaService = new JdaService();
    commandRegistry = new CommandRegistrator();
  }

  public static JdaService getJdaService() {
    return jdaService;
  }

  public static CommandRegistrator getCommandRegistry() {
    return commandRegistry;
  }
}
