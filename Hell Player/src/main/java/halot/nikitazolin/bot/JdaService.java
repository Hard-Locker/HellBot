package halot.nikitazolin.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import halot.nikitazolin.bot.listener.ReadyListener;
import halot.nikitazolin.bot.slashCommand.manager.SlashCommandHandler;

@Component
public class JdaService {

  private String BOT_TOKEN;
  private static JDA jda;

  private void readTokenFromFile() throws IOException {
    BOT_TOKEN = new String(Files.readAllBytes(Paths.get("src/main/resources/bot-token.txt"))).trim();
  }

  public JdaService() {
    try {
      readTokenFromFile();
    } catch (IOException e) {
      e.printStackTrace();
    }

    start(Collections.emptyList());
  }

  private void start(List<GatewayIntent> gatewayIntents) {
    try {
      jda = JDABuilder.createDefault(BOT_TOKEN).setActivity(Activity.watching("на твою мамку"))
          .enableIntents(gatewayIntents).addEventListeners(new SlashCommandHandler(), new ReadyListener()).build();

      jda.awaitReady();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public Optional<JDA> getJda() {
    return Optional.ofNullable(jda);
  }
}
