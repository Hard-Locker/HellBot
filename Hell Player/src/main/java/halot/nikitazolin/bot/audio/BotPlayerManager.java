package halot.nikitazolin.bot.audio;

import java.nio.ByteBuffer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audio.AudioSendHandler;

@Component
@Scope("singleton")
@Getter
@Slf4j
public class BotPlayerManager implements AudioSendHandler {
  
  private final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
  private AudioPlayer audioPlayer;
  private AudioFrame lastFrame;
  
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
  
  protected void createPlayer() {
    AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    AudioSourceManagers.registerLocalSource(audioPlayerManager);
    audioPlayer = audioPlayerManager.createPlayer();
  }
  
  protected void stopPlayingMusic() {
    audioPlayer.stopTrack();
  }
  
  protected void skipTrack() {
    //TODO
  }
  
  protected void skipTrackToPosition() {
    //TODO
  }
  
  protected void setVolume() {
    //TODO
  }
  
  protected void clearQueue() {
    //TODO
  }
  
  protected void shutdownPlayer() {
    audioPlayer.destroy();
    audioPlayerManager.shutdown();
  }
}
