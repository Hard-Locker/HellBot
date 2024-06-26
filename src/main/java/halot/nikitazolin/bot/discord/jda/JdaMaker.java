package halot.nikitazolin.bot.discord.jda;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.listener.ReadyListener;
import halot.nikitazolin.bot.init.authorization.model.AuthorizationData;
import halot.nikitazolin.bot.localization.action.command.common.CommonProvider;
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
public class JdaMaker {

  private final AuthorizationData authorizationData;
  private final CommonProvider commonProvider;
  
  private String token;
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
    String defaultActivity = commonProvider.getText("about_command.description") + " /about";

    try {
      jda = JDABuilder.createDefault(token, gatewayIntents)
          .setActivity(Activity.customStatus(defaultActivity))
          .enableIntents(gatewayIntents)
          .addEventListeners(new ReadyListener())
          .enableCache(cacheFlags)
          .build();

      jda.awaitReady();
    } catch (Exception e) {
      log.error("Error with create JDA: ", e);
    }
  }

  private void readTokenFromFile() {
    token = authorizationData.getDiscordApi().getApiKey();
  }

  public Optional<JDA> getJda() {
    return Optional.ofNullable(jda);
  }
}
