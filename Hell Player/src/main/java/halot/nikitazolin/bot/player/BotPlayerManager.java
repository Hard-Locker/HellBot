package halot.nikitazolin.bot.player;

import java.nio.ByteBuffer;

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
@Getter
@Slf4j
public class BotPlayerManager implements AudioSendHandler {
  
  private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
  private AudioPlayer audioPlayer;
  private AudioFrame lastFrame;
  
  public BotPlayerManager() {
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
    audioPlayer = playerManager.createPlayer();
    
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
}
