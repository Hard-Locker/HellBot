package halot.nikitazolin.bot.command.commands.music;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import halot.nikitazolin.bot.audio.BotAudioService;
import halot.nikitazolin.bot.audio.FillQueueHandler;
import halot.nikitazolin.bot.audio.IPlayerManager;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class PlayCommand extends BotCommand {

  private final IPlayerManager botPlayerManager;
  private final BotAudioService botAudioService;
  private final MessageUtil messageUtil;

  @Override
  public String name() {
    return "play";
  }

  @Override
  public List<String> nameAliases() {
    return List.of("play", "1");
  }

  @Override
  public List<String> commandPrefixes() {
    return List.of("!", "1");
  }

  @Override
  public String description() {
    return "Start playing music from link";
  }

  @Override
  public String requiredRole() {
    return null;
  }

  @Override
  public Permission neededPermission() {
    return Permission.USE_APPLICATION_COMMANDS;
  }

  @Override
  public boolean guildOnly() {
    return true;
  }

  @Override
  public OptionData[] options() {
    return new OptionData[] { new OptionData(OptionType.STRING, "link", "URL with content", false) };
  }

  @Override
  public void execute(BotCommandContext context) {
    AudioPlayer audioPlayer = botPlayerManager.getAudioPlayer();

    String url1 = "D:\\Music\\Folders\\2024\\Kidd Russell - Fade (Минус).mp3";
    String url2 = "D:\\Music\\Folders\\2023\\30 Seconds To Mars - Attack.mp3";
    String url3 = "https://youtu.be/kS-Mob5Ha64?si=qlEmw8tKoEmmQhHs";
    
//    List<String> links = context.getArgumentMapper().getString();
    List<String> links = List.of(url1, url2, url3);
    System.out.println("links size: " + links.size());
    
    for (String trackUrl : links) {
      botPlayerManager.getAudioPlayerManager().loadItemSync(trackUrl, new FillQueueHandler(botPlayerManager));
    }
    
    botAudioService.connectToVoiceChannel(context);
    botAudioService.getBotPlayerManager().startPlayingMusic();
    
    EmbedBuilder embed = messageUtil.createSuccessEmbed("Play: " + audioPlayer.getPlayingTrack().getIdentifier());
    context.sendMessageEmbed(embed);
    
    log.debug("User launched audiotrack." + " User: " + context.getUser() + " Track: " + audioPlayer.getPlayingTrack().getIdentifier());
  }
}