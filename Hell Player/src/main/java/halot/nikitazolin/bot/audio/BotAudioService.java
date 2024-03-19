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

  public boolean connectToVoiceChannel(BotCommandContext context) {
    VoiceChannel userVoiceChannel = getVoiceChannelByUser(context);

    if (checkValidChannel(context, userVoiceChannel) == true) {
      audioManager.openAudioConnection(userVoiceChannel);
      
      log.debug("VoiceChannel is valid. Bot connected to user VoiceChannel");
      return true;
    } else {
      log.debug("VoiceChannel is invalid. Bot won't connect to VoiceChannel");
      return false;
    }
  }

  public void stopAudioSending() {
    botPlayerManager.stopPlayingMusic();
    audioManager.closeAudioConnection();
  }
  
  public void shutdown() {
    stopAudioSending();
    botPlayerManager.shutdownPlayer();
  }

  private VoiceChannel getVoiceChannelByUser(BotCommandContext context) {
    VoiceChannel userVoiceChannel;

    try {
      userVoiceChannel = context.getMember().getVoiceState().getChannel().asVoiceChannel();

      return userVoiceChannel;
    } catch (NullPointerException e) {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", you need to be in voice channel to use music command.");
      context.sendMessageEmbed(embed);

      log.info("User must to be in a voice channel to use music command.");
      return null;
    }
  }
  
  private boolean checkValidChannel(BotCommandContext context, VoiceChannel userVoiceChannel) {
    VoiceChannel botVoiceChannel = null;
    VoiceChannel afkVoiceChannel = context.getGuild().getAfkChannel();
    
    try {
      botVoiceChannel = context.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel();
    } catch (NullPointerException e) {
      log.debug("The bot is not in the voice channel");
    }

    if (afkVoiceChannel != null && afkVoiceChannel.equals(userVoiceChannel)) {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", this command cannot be used in the AFK channel.");
      context.sendMessageEmbed(embed);

      log.debug("User try call bot in AFK channel. User: " + context.getUser());
      return false;
    } else if (botVoiceChannel != null && !botVoiceChannel.equals(userVoiceChannel)) {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", you must be in the same voice channel as the bot to use this command.");
      context.sendMessageEmbed(embed);

      log.debug("User try call bot in different channel. User:" + context.getUser());
      return false;
    } else {
      return true;
    }
  }
}
