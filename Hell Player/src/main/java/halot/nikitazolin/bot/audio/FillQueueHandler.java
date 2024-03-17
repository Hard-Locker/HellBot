package halot.nikitazolin.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

//@RequiredArgsConstructor
public class FillQueueHandler implements AudioLoadResultHandler {

  private final BotAudioService botAudioService;

  public FillQueueHandler(BotAudioService botAudioService) {
    this.botAudioService = botAudioService;
  }
  
  @Override
  public void trackLoaded(AudioTrack track) {
    botAudioService.getBotPlayerManager().getQueue().add(track);
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
