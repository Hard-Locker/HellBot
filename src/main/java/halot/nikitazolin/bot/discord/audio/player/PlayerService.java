package halot.nikitazolin.bot.discord.audio.player;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration.ResamplingQuality;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import dev.lavalink.youtube.clients.skeleton.Client;
import halot.nikitazolin.bot.ApplicationRunnerImpl;
import halot.nikitazolin.bot.discord.DatabaseService;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
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

  private final DatabaseService databaseService;
  private final Settings settings;
  private final SettingsSaver settingsSaver;

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
    AudioSourceManagers.registerLocalSource(audioPlayerManager);

    YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true,
        new Client[] { new MusicWithThumbnail(), new WebWithThumbnail(), new AndroidWithThumbnail() });
    audioPlayerManager.registerSourceManager(youtube);

    audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
    audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
    audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
    audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
    audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
    audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
    audioPlayerManager.registerSourceManager(new NicoAudioSourceManager());
    audioPlayerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));

    audioPlayerManager.getConfiguration().setResamplingQuality(ResamplingQuality.HIGH);
    audioPlayerManager.getConfiguration().setOpusEncodingQuality(10);

    audioPlayer = audioPlayerManager.createPlayer();
    setVolume(settings.getVolume());

    log.info("Created PlayerService for implementation AudioSendHandler");
  }

  public void play() {
    if ((audioPlayer.isPaused() == false) && (audioPlayer.getPlayingTrack() == null)) {
      AudioItemContext audioItemContext = queue.poll();

      audioPlayerManager.loadItem(audioItemContext.url(),
          new AudioLoadResultManager(audioPlayer, audioItemContext.context(), databaseService));
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
    if (positions.isEmpty() == true) {
      return;
    }

    List<AudioItemContext> tempList = new ArrayList<>(queue);
    Collections.sort(positions, Collections.reverseOrder());

    for (int position : positions) {
      if (position >= 0 && position < tempList.size()) {
        tempList.remove(position);
      }
    }

    queue.clear();
    queue.addAll(tempList);
  }

  public void pause() {
    if (audioPlayer.isPaused() == false) {
      audioPlayer.setPaused(true);
    } else {
      audioPlayer.setPaused(false);
    }
  }

  public void setVolume(int volume) {
    if (volume > 0 && volume <= 150) {
      audioPlayer.setVolume(volume);
      settings.setVolume(volume);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
    }
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

  public void fillQueue(List<String> links, BotCommandContext context) {
    for (String link : links) {
      AudioItemContext audioItemContext = new AudioItemContext(link, context);
      boolean result = queue.offer(audioItemContext);

      if (result == true) {
        log.debug("Add link {} to queue", link);
      } else {
        log.warn("Problem with adding link {} to queue", link);
      }
    }
  }
}
