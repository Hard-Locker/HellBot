package halot.nikitazolin.bot.audio.player;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class FillQueueHandler implements AudioLoadResultHandler {

  private final IPlayerService botPlayerManager;

  @Override
  public void trackLoaded(AudioTrack track) {
    botPlayerManager.getQueue().add(track);

    log.debug("Add track: " + track);
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
