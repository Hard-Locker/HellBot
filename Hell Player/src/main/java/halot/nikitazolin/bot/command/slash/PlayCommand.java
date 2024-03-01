package halot.nikitazolin.bot.command.slash;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import halot.nikitazolin.bot.player.BotAudioHandler;
import halot.nikitazolin.bot.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

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
    Member member = event.getMember();
    User user = event.getMember().getUser();
    VoiceChannel voiceChannel;

    if (guild == null || member == null) {
      return;
    }

    try {
      voiceChannel = member.getVoiceState().getChannel().asVoiceChannel();
    } catch (NullPointerException e) {
      event.replyEmbeds(MessageUtils.createInfoEmbed(user.getName() + " need to be in a voice channel to use music command.").build()).queue();
      log.warn("User must to be in a voice channel to use music command.");

      return;
    }

    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);

    AudioManager audioManager = guild.getAudioManager();
    audioManager.openAudioConnection(voiceChannel);

    AudioPlayer player = playerManager.createPlayer();

    BotAudioHandler audioHandler = new BotAudioHandler(player);
    player.addListener(audioHandler);
    audioManager.setSendingHandler(audioHandler);

    String trackUrl = "D:\\Music\\Folders\\2023\\30 Seconds To Mars - Attack.mp3";

    playerManager.loadItem(trackUrl, new AResultHandler(event));

  }

  @RequiredArgsConstructor
  class AResultHandler implements AudioLoadResultHandler {
    
    private final SlashCommandInteractionEvent event;

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
      BotAudioHandler audioHandler = (BotAudioHandler) event.getGuild().getAudioManager().getSendingHandler();
      AudioPlayer audioPlayer = audioHandler.getAudioPlayer();
      
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