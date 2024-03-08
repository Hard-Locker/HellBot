package halot.nikitazolin.bot.audio;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

@Component
@Getter
@Slf4j
public class BotAudioService {

  private final Guild initialGuild;
  private final BotPlayerManager audioSendHandler = new BotPlayerManager();
  private AudioManager audioManager;

  public BotAudioService(Guild guild) {
    this.initialGuild = guild;

    audioManager = guild.getAudioManager();
    setUpAudioSendHandler(audioManager);
  }

  private void setUpAudioSendHandler(AudioManager audioManager) {
    if (audioManager.getSendingHandler() == null) {
      audioManager.setSendingHandler(audioSendHandler);
      log.debug("Set sending handler for guild: " + initialGuild.getId());
    }
  }

  public void connectToVoiceChannel(BotCommandContext context) {
    VoiceChannel voiceChannel = getVoiceChannelByUser(context);

    audioManager.openAudioConnection(voiceChannel);
  }

  public void stopAudioSending() {
    audioSendHandler.stopPlayingMusic();
    audioManager.closeAudioConnection();
  }

  //TODO Remove event
  private VoiceChannel getVoiceChannelByUser(BotCommandContext context) {
    SlashCommandInteractionEvent event = context.getSlashCommandEvent();
    
    Guild guild = context.getGuild();
    Member member = context.getMember();
    User user = context.getUser();
    VoiceChannel userVoiceChannel;
    VoiceChannel afkVoiceChannel = guild.getAfkChannel();

    try {
      userVoiceChannel = member.getVoiceState().getChannel().asVoiceChannel();

      if (afkVoiceChannel != null && afkVoiceChannel.equals(userVoiceChannel)) {
        event.replyEmbeds(MessageUtil.createErrorEmbed(user.getName() + ", this command cannot be used in the AFK channel.").build()).queue();
        return null;
      }
      
      return userVoiceChannel;
    } catch (NullPointerException e) {
      event.replyEmbeds(MessageUtil.createErrorEmbed(user.getName() + ", you need to be in voice channel to use music command.").build()).queue();
      log.warn("User must to be in a voice channel to use music command.");
      return null;
    }
  }

  public void shutdown() {
    stopAudioSending();
    audioSendHandler.shutdownPlayer();
  }

  public void rebootPlayer() {
    stopAudioSending();
    audioSendHandler.shutdownPlayer();
    audioSendHandler.createPlayer();
  }
}
