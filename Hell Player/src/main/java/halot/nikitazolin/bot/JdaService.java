package halot.nikitazolin.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.manager.SlashCommandHandler;
import halot.nikitazolin.bot.listener.ReadyListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Component
@Slf4j
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
      log.error("Error read token: ", e);
    }

    start(Collections.emptyList());
  }

  private void start(List<GatewayIntent> gatewayIntents) {
    try {
      jda = JDABuilder.createDefault(BOT_TOKEN)
          .setActivity(Activity.watching("на твою мамку"))
          .enableIntents(gatewayIntents)
          .addEventListeners(new SlashCommandHandler(), new ReadyListener())
          .enableCache(CacheFlag.VOICE_STATE)
          .build();

      jda.awaitReady();
    } catch (InterruptedException e) {
      log.error("Interrupt: ", e);
    }
  }

  public Optional<JDA> getJda() {
    return Optional.ofNullable(jda);
  }
}
