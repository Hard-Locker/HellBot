package halot.nikitazolin.bot.audio;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
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
public class BotAudioService {

  private final MessageUtil messageUtil;
  private final IPlayerManager botPlayerManager;
  private AudioManager audioManager;

  public void registratePlayer(Guild guild) {
    audioManager = guild.getAudioManager();
    setUpAudioSendHandler(audioManager, guild);
  }

  private void setUpAudioSendHandler(AudioManager audioManager, Guild guild) {
    if (audioManager.getSendingHandler() == null) {
      audioManager.setSendingHandler(botPlayerManager);

      log.debug("Set sending handler for guild: " + guild.getId());
    }
  }

  // TODO need to check bot voice status
  public void connectToVoiceChannel(BotCommandContext context) {
    VoiceChannel voiceChannel = getVoiceChannelByUser(context);

    audioManager.openAudioConnection(voiceChannel);
  }

  public void stopAudioSending() {
    botPlayerManager.stopPlayingMusic();
    audioManager.closeAudioConnection();
  }

  private VoiceChannel getVoiceChannelByUser(BotCommandContext context) {
    VoiceChannel userVoiceChannel;
    VoiceChannel afkVoiceChannel = context.getGuild().getAfkChannel();

    try {
      userVoiceChannel = context.getMember().getVoiceState().getChannel().asVoiceChannel();

      if (afkVoiceChannel != null && afkVoiceChannel.equals(userVoiceChannel)) {
        EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", this command cannot be used in the AFK channel.");
        context.sendMessageEmbed(embed);
        return null;
      }

      return userVoiceChannel;
    } catch (NullPointerException e) {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", you need to be in voice channel to use music command.");
      context.sendMessageEmbed(embed);

      log.info("User must to be in a voice channel to use music command.");
      return null;
    }
  }

  public void shutdown() {
    stopAudioSending();
    botPlayerManager.shutdownPlayer();
  }
}
