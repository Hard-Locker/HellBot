package halot.nikitazolin.bot.discord.audio.player;

import java.time.LocalDateTime;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import halot.nikitazolin.bot.discord.DatabaseService;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.repository.model.SongHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AudioLoadResultManager implements AudioLoadResultHandler {

  private final AudioPlayer audioPlayer;
  private final BotCommandContext context;
  private final DatabaseService databaseService;

  @Override
  public void trackLoaded(AudioTrack track) {
    audioPlayer.playTrack(track);
    log.debug("Play track: " + track);

    saveSongHistoryToDb(track);
  }

  @Override
  public void playlistLoaded(AudioPlaylist playlist) {
    log.debug("Loaded playlist");
  }

  @Override
  public void noMatches() {
    log.debug("Not found music");
  }

  @Override
  public void loadFailed(FriendlyException exception) {
    log.debug("Failed to load music");
  }

  private void saveSongHistoryToDb(AudioTrack track) {
    String url = track.getInfo().uri;
    String author = track.getInfo().author;
    String title = track.getInfo().title;
    Long length = track.getInfo().length;
    Long userId = context.getUser().getIdLong();
    Long guildId = context.getGuild().getIdLong();

    SongHistory songHistory = new SongHistory(LocalDateTime.now(), url, author, title, length, userId, guildId);
    databaseService.saveSongHistoryToDb(songHistory);

    log.debug("SongHistory saved to database.");
  }
}