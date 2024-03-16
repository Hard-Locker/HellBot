package halot.nikitazolin.bot.audio;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

//@Component
//@Scope("singleton")
@Getter
@Slf4j
public class BotPlayerManager implements IPlayerManager {

  private AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
  private AudioPlayer audioPlayer;
  private AudioFrame lastFrame;
  private BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();

//  @PostConstruct
//  public void init() {
//    System.out.println("BotPlayerManager before createPlayer");
//    createPlayer();
//
//    log.info("Created BotPlayerManager for implementation AudioSendHandler");
//
//    System.out.println("BotPlayerManager is initialized.");
//  }
  
  public BotPlayerManager() {
    createPlayer();

    log.info("Created BotPlayerManager for implementation AudioSendHandler");
  }

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
    
//    TrackScheduler trackScheduler = new TrackScheduler(audioPlayer, queue);
//    audioPlayer.addListener(trackScheduler);
  }

  public void startPlayingMusic() {
    System.out.println("Called startPlayingMusic");
    if ((audioPlayer.isPaused() == false) && (audioPlayer.getPlayingTrack() == null)) {
      System.out.println("Start queue size: " + queue.size());
      audioPlayer.playTrack(queue.poll());
    }
  }

  public void stopPlayingMusic() {
    queue.clear();
    audioPlayer.stopTrack();
  }

  public void skipTrack() {
    System.out.println("Called skipTrack");
    System.out.println("Before skip queue size: " + queue.size());
    audioPlayer.stopTrack();
    System.out.println("After skip  queue size: " + queue.size());
//    audioPlayer.playTrack(queue.poll());
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
