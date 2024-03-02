package halot.nikitazolin.bot.audio;

import org.springframework.stereotype.Component;

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

  private final Guild guild;
  private final BotPlayerManager audioSendHandler = new BotPlayerManager();
  private AudioManager audioManager;
  
  public BotAudioService(Guild guild) {
    this.guild = guild;
    
    audioManager = guild.getAudioManager();
    setUpAudioSendHandler(audioManager);
  }
  
  private void setUpAudioSendHandler(AudioManager audioManager) {
    if (audioManager.getSendingHandler() == null) {
      audioManager.setSendingHandler(audioSendHandler);
      log.debug("Set sending handler for guild: " + guild.getId());
    }
  }

  public void connectToVoiceChannel(SlashCommandInteractionEvent event) {
    VoiceChannel voiceChannel = getVoiceChannelByUser(event);

    audioManager.openAudioConnection(voiceChannel);
  }
  
  public void stopAudioSending() {
    audioSendHandler.stopPlayingMusic();
    audioManager.closeAudioConnection();
  }

  private VoiceChannel getVoiceChannelByUser(SlashCommandInteractionEvent event) {
    Guild guild = event.getGuild();
    Member member = event.getMember();
    User user = event.getMember().getUser();
    VoiceChannel voiceChannel;

    if (guild == null || member == null) {
      log.error("Not found member or guild");

      return null;
    }

    try {
      voiceChannel = member.getVoiceState().getChannel().asVoiceChannel();

      return voiceChannel;
    } catch (NullPointerException e) {
      event.replyEmbeds(MessageUtil.createInfoEmbed(user.getName() + " need to be in a voice channel to use music command.").build()).queue();
      log.warn("User must to be in a voice channel to use music command.");

      return null;
    }
  }

}
