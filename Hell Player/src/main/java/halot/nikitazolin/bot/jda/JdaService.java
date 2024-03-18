package halot.nikitazolin.bot.jda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.listener.ReadyListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class JdaService {

  private String BOT_TOKEN;
  private String status = "on you";
  private final List<GatewayIntent> gatewayIntents = List.of(
      GatewayIntent.GUILD_MESSAGES,
      GatewayIntent.DIRECT_MESSAGES,
      GatewayIntent.MESSAGE_CONTENT,
      GatewayIntent.GUILD_MESSAGE_REACTIONS,
      GatewayIntent.DIRECT_MESSAGE_REACTIONS,
      GatewayIntent.GUILD_VOICE_STATES,
      GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
      GatewayIntent.SCHEDULED_EVENTS
      );
  private final List<CacheFlag> cacheFlags = List.of(
      CacheFlag.VOICE_STATE,
      CacheFlag.EMOJI,
      CacheFlag.STICKER,
      CacheFlag.SCHEDULED_EVENTS
      );
  
  private JDA jda;
  
  public void makeJda() {
    readTokenFromFile();
    createJda();
  }

  private void createJda() {
    try {
      jda = JDABuilder.createDefault(BOT_TOKEN, gatewayIntents)
          .setActivity(Activity.watching(status))
          .enableIntents(gatewayIntents)
          .addEventListeners(new ReadyListener())
          .enableCache(cacheFlags)
          .build();

      jda.awaitReady();
    } catch (InterruptedException e) {
      log.error("Interrupt: ", e);
    }
  }

  private void readTokenFromFile() {
    try {
      BOT_TOKEN = new String(Files.readAllBytes(Paths.get("src/main/resources/bot-token.txt"))).trim();
    } catch (IOException e) {
      log.error("Error read token: ", e);
    }
  }

  public Optional<JDA> getJda() {
    return Optional.ofNullable(jda);
  }
}
