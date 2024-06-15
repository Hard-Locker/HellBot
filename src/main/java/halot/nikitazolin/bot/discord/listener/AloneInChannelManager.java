package halot.nikitazolin.bot.discord.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class AloneInChannelManager {

  private final GuildAudioService guildAudioService;
  private final Settings settings;

  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> disconnectTask;

  public void processingAlone(GuildVoiceUpdateEvent guildVoiceUpdateEvent) {
    AudioChannelUnion joinedChannel = guildVoiceUpdateEvent.getChannelJoined();
    AudioChannelUnion leftChannel = guildVoiceUpdateEvent.getChannelLeft();

    if (joinedChannel != null) {
      if (guildVoiceUpdateEvent.getMember().getUser().isBot()) {
        log.info("Bot joined channel: {}", joinedChannel.getName());
        startIdleCheck(joinedChannel);
      } else if (isBotAlone(joinedChannel)) {
        log.info("Non-bot user joined, cancelling idle check for channel: {}", joinedChannel.getName());
        cancelIdleCheck();
      }
    }

    if (leftChannel != null) {
      if (guildVoiceUpdateEvent.getMember().getUser().isBot()) {
        log.info("Bot left channel: {}", leftChannel.getName());
        cancelIdleCheck();
      } else if (isBotAlone(leftChannel)) {
        log.info("Non-bot user left, starting idle check for channel: {}", leftChannel.getName());
        startIdleCheck(leftChannel);
      }
    }
  }

  private boolean isBotAlone(AudioChannel voiceChannel) {
    long nonBotMembers = voiceChannel.getMembers().stream().filter(member -> !member.getUser().isBot()).count();
    boolean botAlone = nonBotMembers == 0;
    log.debug("Bot alone status for channel {}: {}", voiceChannel.getName(), botAlone);

    return botAlone;
  }

  private void startIdleCheck(AudioChannel voiceChannel) {
    cancelIdleCheck();

    log.info("Starting idle check for channel: {}", voiceChannel.getName());
    disconnectTask = scheduler.schedule(() -> {
      if (isBotAlone(voiceChannel)) {
        log.info("Bot has been alone in channel {} for {} seconds, disconnecting.", voiceChannel.getName(),
            settings.getAloneTimeUntilStop());
        guildAudioService.stopAudioSending();
      }
    }, settings.getAloneTimeUntilStop(), TimeUnit.SECONDS);
  }

  private void cancelIdleCheck() {
    if (disconnectTask != null && !disconnectTask.isDone()) {
      log.info("Cancelling idle check.");
      disconnectTask.cancel(true);
    }
  }
}
