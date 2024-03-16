package halot.nikitazolin.bot.audio;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

//@Component
//@Scope("singleton")
@Getter
@Slf4j
//@RequiredArgsConstructor
public class BotAudioService {

  private final BotPlayerManager botPlayerManager = new BotPlayerManager();

//  private final IPlayerManager botPlayerManager;
  private AudioManager audioManager;

  public BotAudioService(Guild guild) {
    audioManager = guild.getAudioManager();
    setUpAudioSendHandler(audioManager, guild);
  }
  
//  @PostConstruct
//  public void init(BotCommandContext context) {
//    System.out.println("BotAudioService before initialize audioManager");
//    
////    Guild guild = HellBot.getJdaService().getJda().get().getGuilds().getFirst();
//    Guild guild = context.getGuild();
//    
//    audioManager = guild.getAudioManager();
//    setUpAudioSendHandler(audioManager, guild);
//
//    System.out.println("guild: " + guild);
//    System.out.println("BotAudioService is initialized.");
//  }

  private void setUpAudioSendHandler(AudioManager audioManager, Guild guild) {
    if (audioManager.getSendingHandler() == null) {
      audioManager.setSendingHandler(botPlayerManager);
      
      log.debug("Set sending handler for guild: " + guild.getId());
    }
  }

  //TODO need to check bot voice status
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
        EmbedBuilder embed = MessageUtil.createErrorEmbed(context.getUser().getAsMention() + ", this command cannot be used in the AFK channel.");
        context.sendMessageEmbed(embed);
        return null;
      }

      return userVoiceChannel;
    } catch (NullPointerException e) {
      EmbedBuilder embed = MessageUtil.createErrorEmbed(context.getUser().getAsMention() + ", you need to be in voice channel to use music command.");
      context.sendMessageEmbed(embed);

      log.warn("User must to be in a voice channel to use music command.");
      return null;
    }
  }

  public void shutdown() {
    stopAudioSending();
    botPlayerManager.shutdownPlayer();
  }

  public void rebootPlayer() {
    stopAudioSending();
    botPlayerManager.shutdownPlayer();
    botPlayerManager.createPlayer();
  }
}
