package halot.nikitazolin.bot.discord.audio.player;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueueFiller {

  private final IPlayerService playerService;

  public void makeTracks(List<String> identifiers) {
    List<AudioReference> references = new ArrayList<>();

    for (String url : identifiers) {
      references.add(new AudioReference(url, null));
    }

    log.debug("");

    for (AudioReference reference : references) {
      
//      playerService.getAudioPlayerManager().loadItemOrdered("Key", reference, new TrackLoader(playerService));
    }
  }

  
  
  
  @RequiredArgsConstructor
  private class TrackLoader implements AudioLoadResultHandler {

    private final IPlayerService playerService;

    @Override
    public void trackLoaded(AudioTrack track) {
      playerService.getQueue().add(track);

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
}
