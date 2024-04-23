package halot.nikitazolin.bot.discord.audio.player;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.ResamplingQuality;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import halot.nikitazolin.bot.discord.DatabaseFillService;
import halot.nikitazolin.bot.discord.command.BotCommandContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audio.AudioSendHandler;

@Service
@Scope("singleton")
@Getter
@Slf4j
@RequiredArgsConstructor
public class PlayerService implements AudioSendHandler {

  private final DatabaseFillService databaseFillService;

  private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
  private AudioPlayer audioPlayer;
  private AudioFrame lastFrame;
  private BlockingQueue<AudioItemContext> queue = new LinkedBlockingQueue<>();

  @Override
  public boolean canProvide() {
    lastFrame = audioPlayer.provide();
    return lastFrame != null;
  }

  @Override
  public ByteBuffer provide20MsAudio() {
    return ByteBuffer.wrap(lastFrame.getData());
  }

  @Override
  public boolean isOpus() {
    return true;
  }

  public void createPlayer() {
    AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    AudioSourceManagers.registerLocalSource(audioPlayerManager);
    audioPlayerManager.source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);

    audioPlayerManager.getConfiguration().setResamplingQuality(ResamplingQuality.HIGH);
    audioPlayerManager.getConfiguration().setOpusEncodingQuality(10);

    audioPlayer = audioPlayerManager.createPlayer();

    log.info("Created PlayerService for implementation AudioSendHandler");
  }

  public void play() {
    if ((audioPlayer.isPaused() == false) && (audioPlayer.getPlayingTrack() == null)) {
      AudioItemContext audioItemContext = queue.poll();

      audioPlayerManager.loadItem(audioItemContext.getUrl(), new AudioLoadResultManager(audioPlayer, audioItemContext.getContext(), databaseFillService));
    } else if (audioPlayer.isPaused() == true) {
      audioPlayer.setPaused(false);
    }
  }

  public void stop() {
    audioPlayer.stopTrack();
  }

  public void skipTrack() {
    audioPlayer.stopTrack();
    play();
  }

  public void skipTracks(List<Integer> positions) {
    // TODO
  }

  public void pause() {
    if (audioPlayer.isPaused() == false) {
      audioPlayer.setPaused(true);
    } else {
      audioPlayer.setPaused(false);
    }
  }

  public void setVolume(int volume) {
    audioPlayer.setVolume(volume);
  }

  public void clearQueue() {
    queue.clear();
  }

  public void offPlayer() {
    queue.clear();
    audioPlayer.stopTrack();
  }

  public void shutdownPlayer() {
    audioPlayer.destroy();
    audioPlayerManager.shutdown();
  }

  public void fillQueue(List<String> identifiers, BotCommandContext context) {
    for (String identifier : identifiers) {
      AudioItemContext audioItemContext = new AudioItemContext(identifier, context);
      boolean result = queue.offer(audioItemContext);

      if (result) {
        log.debug("Add link {} to queue", identifier);
      } else {
        log.warn("Problem with adding link {} to queue", identifier);
      }
    }
  }
}
