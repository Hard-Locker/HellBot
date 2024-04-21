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

import halot.nikitazolin.bot.init.discord.DatabaseFillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class TrackScheduler extends AudioEventAdapter implements AudioEventListener {

  private final DatabaseFillService databaseFillService;
  private final IPlayerService playerService;

  private boolean isRepeat = false;
  
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
    System.out.println("Event onTrackStart");
    System.out.println("uri: " + track.getInfo().uri);
    System.out.println("length: " + track.getInfo().length);
    System.out.println("author: " + track.getInfo().author);
    System.out.println("title: " + track.getInfo().title);
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason == AudioTrackEndReason.FINISHED) {
      playerService.startPlayingMusic();
//      playerService.getAudioPlayerManager().loadItem(playerService.getQueue().poll(), new AudioLoadResultManager(playerService.getAudioPlayer()));
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
