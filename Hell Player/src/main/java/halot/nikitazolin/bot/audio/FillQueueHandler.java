package halot.nikitazolin.bot.audio;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;

//@Component
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
