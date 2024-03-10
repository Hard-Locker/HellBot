package halot.nikitazolin.bot.command.commands.music;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import halot.nikitazolin.bot.audio.BotAudioService;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Slf4j
public class PlayCommand extends BotCommand {

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
    return new OptionData[] {
        new OptionData(OptionType.STRING, "link", "URL with audio", false)
    };
  }

  @Override
  public void execute(BotCommandContext context) {
//    SlashCommandInteractionEvent slashEvent = context.getSlashCommandEvent();
    Guild guild = context.getGuild();
    BotAudioService botAudioService = new BotAudioService(guild);
    AudioPlayer audioPlayer = botAudioService.getAudioSendHandler().getAudioPlayer();
    
    String trackUrl;
//    trackUrl = "D:\\Music\\Folders\\2023\\30 Seconds To Mars - Attack.mp3";
    
    List<OptionMapping> options = context.getOptions();
    OptionMapping option;
//    options.stream().filter(opt -> opt.getName().equals("link"));
//    List<String> opt = option.ge;
    
    String reason = null;
    
    for (OptionMapping opt : options) {
      if (opt.getName().equals("link")) {
        option = opt;
        
        reason = opt.getAsString();
      }
    }
    
//    String reason = slashEvent.getOption("link") != null ? slashEvent.getOption("link").getAsString() : "No link provided";
    trackUrl = reason;
    System.out.println(reason);
    
    botAudioService.connectToVoiceChannel(context);
    botAudioService.getAudioSendHandler().getAudioPlayerManager().loadItem(trackUrl, new PlayResultHandler(audioPlayer));
    
    EmbedBuilder embed = MessageUtil.createSuccessEmbed("Play: " + trackUrl);
    context.sendMessageEmbed(embed);
//    slashEvent.replyEmbeds(MessageUtil.createInfoEmbed("Play: " + trackUrl).build()).queue();
    
    log.debug("User launched audiotrack." + " User: " + context.getUser() + " Track: " + trackUrl);
  }

  @RequiredArgsConstructor
  class PlayResultHandler implements AudioLoadResultHandler {

    private final AudioPlayer audioPlayer;

    @Override
    public void trackLoaded(AudioTrack track) {
      loadSingle(track, null);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {

    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException exception) {

    }

    private void loadSingle(AudioTrack track, AudioPlaylist playlist) {
      audioPlayer.playTrack(track);
//      int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;
//      String addMsg = FormatUtil.filter(event.getClient().getSuccess() + " Добавлен **" + track.getInfo().title + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "чтобы начать играть" : " чтобы добавить в очередь на позиции " + pos));
//      
//      if (playlist == null || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION)) {
//        m.editMessage(addMsg).queue();
//      } else {
//        new ButtonMenu.Builder()
//            .setText(addMsg + "\n" + event.getClient().getWarning() + " У этого трека есть плейлист из **" + playlist.getTracks().size() + "** дополнительных треков. Нажмите " + LOAD + ", чтобы начать плейлист.")
//            .setChoices(LOAD, CANCEL).setEventWaiter(bot.getWaiter()).setTimeout(30, TimeUnit.SECONDS).setAction(re -> {
//              if (re.getName().equals(LOAD)) {
//                m.editMessage(addMsg + "\n" + event.getClient().getSuccess() + " Загружено **" + loadPlaylist(playlist, track) + "** дополнительных треков!").queue();
//              } else {
//                m.editMessage(addMsg).queue();
//              }
//            }).setFinalAction(m -> {
//              try {
//                m.clearReactions().queue();
//              } catch (PermissionException ignore) {
//              }
//            }).build().display(m);
//      }
    }
  }

}