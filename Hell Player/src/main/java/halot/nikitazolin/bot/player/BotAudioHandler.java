package halot.nikitazolin.bot.player;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audio.AudioSendHandler;

@Component
@RequiredArgsConstructor
public class BotAudioHandler extends AudioEventAdapter implements AudioSendHandler {
  
  private final AudioPlayer audioPlayer;
  private AudioFrame lastFrame;

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
