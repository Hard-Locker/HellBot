package halot.nikitazolin.bot.audio.player;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public interface IPlayerService extends AudioSendHandler {

  boolean canProvide();

  ByteBuffer provide20MsAudio();

  default boolean isOpus() {
    return true;
  }

  void createPlayer();
  
  void startPlayingMusic();
  
  void stopPlayingMusic();
  
  void skipTrack();
  
  void skipTracks();
  
  void setVolume();
  
  void shutdownPlayer();

  AudioPlayer getAudioPlayer();

  AudioPlayerManager getAudioPlayerManager();

  BlockingQueue<AudioTrack> getQueue();
}
