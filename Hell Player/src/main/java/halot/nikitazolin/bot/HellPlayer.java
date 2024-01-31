package halot.nikitazolin.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import halot.nikitazolin.bot.HellPlayer;
import halot.nikitazolin.bot.JdaService;
import halot.nikitazolin.bot.command.manager.CommandRegistry;
import lombok.Getter;

@SpringBootApplication
public class HellPlayer {

  private static final HellPlayer instance = new HellPlayer();
  private static JdaService jdaService;
  private static CommandRegistry commandRegistry;

  public static void main(String[] args) {

    jdaService = new JdaService();
    commandRegistry = new CommandRegistry();

//    ApplicationContext context = SpringApplication.run(HellPlayer.class, args);
//    Player player = context.getBean(Player.class);
//
//    player.play();
  }

  public static HellPlayer getInstance() {
    return instance;
  }

  public static JdaService getJdaService() {
    return jdaService;
  }

  public static CommandRegistry getCommandRegistry() {
    return commandRegistry;
  }
}
