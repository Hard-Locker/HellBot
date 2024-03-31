package halot.nikitazolin.bot.discord.audio.player;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope("singleton")
@Getter
@Slf4j
@RequiredArgsConstructor
public class PlayerService implements IPlayerService {

  private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
  private AudioPlayer audioPlayer;
  private AudioFrame lastFrame;
  private BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

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
    audioPlayer = audioPlayerManager.createPlayer();

    log.info("Created PlayerService for implementation AudioSendHandler");
  }

  public void startPlayingMusic() {
    if ((audioPlayer.isPaused() == false) && (audioPlayer.getPlayingTrack() == null)) {
      audioPlayer.playTrack(queue.poll());
    }
  }

  public void stopPlayingMusic() {
    queue.clear();
    audioPlayer.stopTrack();
  }

  public void skipTrack() {
    audioPlayer.stopTrack();
    audioPlayer.playTrack(queue.poll());
  }

  public void skipTracks() {
    // TODO
  }

  public void setVolume() {
    // TODO
  }

  public void clearQueue() {
    queue.clear();
  }

  public void shutdownPlayer() {
    audioPlayer.destroy();
    audioPlayerManager.shutdown();
  }
}
