package halot.nikitazolin.bot.discord.audio.player;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import halot.nikitazolin.bot.discord.tool.ActivityManager;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class TrackScheduler extends AudioEventAdapter implements AudioEventListener {

  private final PlayerService playerService;
  private final Settings settings;
  private final ActivityManager activityManager;

//  private boolean isRepeat = false;

  @Override
  public void onEvent(AudioEvent event) {
    super.onEvent(event);
  }

  @Override
  public void onPlayerPause(AudioPlayer player) {

  }

  @Override
  public void onPlayerResume(AudioPlayer player) {

  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
    if (settings.isSongInStatus() == true) {
      AudioTrack audioTrack = playerService.getAudioPlayer().getPlayingTrack();
      String song = audioTrack.getInfo().author + " - " + audioTrack.getInfo().title;

      activityManager.setListening(song);
    }
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason == AudioTrackEndReason.FINISHED) {
      log.trace("Track ended, try start new track");

      playerService.play();
    }
  }

  @Override
  public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
    // An already playing track threw an exception (track end event will still be
    // received separately)
  }

  @Override
  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
    // Audio track has been unable to provide us any audio, might want to just start
    // a new track
  }
}
