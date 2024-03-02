package halot.nikitazolin.bot.command.slash;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import halot.nikitazolin.bot.player.BotAudioService;
import halot.nikitazolin.bot.player.BotPlayerManager;
import halot.nikitazolin.bot.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Slf4j
public class PlayCommand extends SlashCommand {

  @Override
  public String name() {
    return "play";
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
    return new OptionData[] {};
  }

  @Override
  public void execute(SlashCommandRecord info) {
    SlashCommandInteractionEvent event = info.slashCommandEvent();
    Guild guild = event.getGuild();
    BotPlayerManager audioHandler = new BotPlayerManager();
    AudioPlayer audioPlayer = audioHandler.getAudioPlayer();
    BotAudioService botAudioService = new BotAudioService(guild, audioHandler);
    
    String trackUrl = "D:\\Music\\Folders\\2023\\30 Seconds To Mars - Attack.mp3";
    
    botAudioService.connectToVoiceChannel(event);
    audioHandler.getPlayerManager().loadItem(trackUrl, new AResultHandler(event, audioPlayer));
    event.replyEmbeds(MessageUtils.createInfoEmbed("Play: " + trackUrl).build()).queue();
  }

  @RequiredArgsConstructor
  class AResultHandler implements AudioLoadResultHandler {

    private final SlashCommandInteractionEvent event;
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