package halot.nikitazolin.bot.discord.audio;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import halot.nikitazolin.bot.discord.tool.ActivityManager;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.DiscordDataReceiver;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.PermissionProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@Service
@Scope("singleton")
@Getter
@Slf4j
@RequiredArgsConstructor
public class GuildAudioService {

  private final ActivityManager activityManager;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final AllowChecker allowChecker;
  private final DiscordDataReceiver discordDataReceiver;
  private final PlayerService playerService;
  private final PermissionProvider permissionProvider;

  private AudioManager audioManager;

  public void registratePlayer(Guild guild) {
    log.info("Start registrate player");
    audioManager = guild.getAudioManager();
    setUpAudioSendHandler(audioManager, guild);
    log.info("Player registrated");
  }

  private void setUpAudioSendHandler(AudioManager audioManager, Guild guild) {
    if (audioManager.getSendingHandler() == null) {
      audioManager.setSendingHandler(playerService);
      log.debug("Set sending handler for guild: " + guild.getId());
    }
  }

  public boolean connectToVoiceChannel(BotCommandContext context) {
    log.info("Try connect to VoiceChannel");
    VoiceChannel userVoiceChannel = discordDataReceiver.getVoiceChannelByMember(context.getMember());

    if (userVoiceChannel == null) {
      EmbedBuilder embed = messageFormatter.createErrorEmbed(
          context.getUser().getAsMention() + ", " + permissionProvider.getText("voice_channel.no_channel"));
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
    }

    if (allowChecker.isAllowedVoiceChannel(context, userVoiceChannel) == true) {
      audioManager.openAudioConnection(userVoiceChannel);
      log.debug("VoiceChannel is valid. Bot connected to user VoiceChannel");

      return true;
    } else {
      log.debug("VoiceChannel is invalid. Bot won't connect to VoiceChannel");
      return false;
    }
  }

  public void stopAudioSending() {
    log.info("Stop audio sending");
    if (settings.isSongInStatus() == true) {
      activityManager.setCustomStatus("Chill");
    }

    playerService.offPlayer();
    audioManager.closeAudioConnection();
  }

  public void shutdown() {
    log.info("Shutdown player");
    stopAudioSending();
    playerService.shutdownPlayer();
  }
}
