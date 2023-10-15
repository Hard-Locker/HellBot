package halot.nikitazolin.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class HellPlayer {

  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(HellPlayer.class, args);
    Player player = context.getBean(Player.class);

    player.play();
  }
}
