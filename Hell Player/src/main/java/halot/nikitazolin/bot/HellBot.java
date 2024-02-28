package halot.nikitazolin.bot;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import halot.nikitazolin.bot.command.manager.CommandRegistry;

@SpringBootApplication
public class HellBot {

  private static final HellBot instance = new HellBot();
  private static JdaService jdaService;
  private static CommandRegistry commandRegistry;

  public static void main(String[] args) {
    jdaService = new JdaService();
    commandRegistry = new CommandRegistry();
  }

  public static HellBot getInstance() {
    return instance;
  }

  public static JdaService getJdaService() {
    return jdaService;
  }

  public static CommandRegistry getCommandRegistry() {
    return commandRegistry;
  }
}
