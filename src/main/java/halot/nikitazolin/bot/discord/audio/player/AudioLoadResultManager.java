package halot.nikitazolin.bot.discord.audio.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AudioLoadResultManager implements AudioLoadResultHandler {

  private final AudioPlayer audioPlayer;

  @Override
  public void trackLoaded(AudioTrack track) {
    audioPlayer.playTrack(track);
    
    log.debug("Play track: " + track);
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
}