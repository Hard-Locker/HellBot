package halot.nikitazolin.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.manager.CommandEventHandler;
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
  private String status = "на твою мамку";
  private JDA jda;
  
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
  
  public JdaService() {
    readTokenFromFile();
    createJda(gatewayIntents, cacheFlags);
  }

  private void createJda(List<GatewayIntent> gatewayIntents, List<CacheFlag> cacheFlags) {
    try {
      jda = JDABuilder.createDefault(BOT_TOKEN, gatewayIntents)
          .setActivity(Activity.watching(status))
          .enableIntents(gatewayIntents)
          .addEventListeners(new CommandEventHandler(), new ReadyListener())
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
