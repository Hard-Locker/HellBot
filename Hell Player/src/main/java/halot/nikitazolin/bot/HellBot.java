package halot.nikitazolin.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import halot.nikitazolin.bot.jda.JdaService;

@SpringBootApplication
public class HellBot {

  private static JdaService jdaService;

  public static void main(String[] args) {
    SpringApplication.run(HellBot.class, args);
  }

  public static JdaService getJdaService() {
    return jdaService;
  }
}
