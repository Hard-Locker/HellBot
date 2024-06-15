package halot.nikitazolin.bot.discord.listener.manager;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.listener.AloneInChannelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenericGuildVoiceEventManager {

  private final AloneInChannelManager aloneInChannelManager;

  public void processingEvent(GenericGuildVoiceEvent genericGuildVoiceEvent) {
    if (genericGuildVoiceEvent instanceof GuildVoiceUpdateEvent) {
      log.debug("Call event: {}", genericGuildVoiceEvent);
      aloneInChannelManager.processingAlone((GuildVoiceUpdateEvent) genericGuildVoiceEvent);
    }
  }
}
