package halot.nikitazolin.bot.discord.audio.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.discord.tool.ActivityManager;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class TrackScheduler extends AudioEventAdapter implements AudioEventListener {

  private final PlayerService playerService;
  private final Settings settings;
  private final ActivityManager activityManager;
  private final JdaMaker jdaMaker;

//  private boolean isRepeat = false;

  @Override
  public void onEvent(AudioEvent event) {
    super.onEvent(event);
  }

  @Override
  public void onPlayerPause(AudioPlayer player) {
    String info = "Player paused";

    if (settings.isSongInStatus() == true) {
      activityManager.setCustomStatus(info);
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<Long> textChannelIds = settings.getAllowedTextChannelIds();
      setTopic(textChannelIds, info);
    }
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
    AudioTrack audioTrack = playerService.getAudioPlayer().getPlayingTrack();
    String song = audioTrack.getInfo().author + " - " + audioTrack.getInfo().title;
    String topic = "Now playing: " + song;

    if (settings.isSongInStatus() == true) {
      activityManager.setListening(song);
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<Long> textChannelIds = settings.getAllowedTextChannelIds();
      setTopic(textChannelIds, topic);
    }
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
    AudioTrack audioTrack = playerService.getAudioPlayer().getPlayingTrack();
    String song = audioTrack.getInfo().author + " - " + audioTrack.getInfo().title;
    String topic = "Now playing: " + song;

    if (settings.isSongInStatus() == true) {
      activityManager.setListening(song);
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<Long> textChannelIds = settings.getAllowedTextChannelIds();
      setTopic(textChannelIds, topic);
    }
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (settings.isSongInStatus() == true && playerService.getQueue().isEmpty() == true) {
      activityManager.setCustomStatus("Chill");
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<Long> textChannelIds = settings.getAllowedTextChannelIds();
      setTopic(textChannelIds, "");
    }

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

  private void setTopic(List<Long> textChannelIds, String topic) {
    log.debug("Updating text channels topic");
    if (textChannelIds.isEmpty() == true) {
      log.debug("Have not text channels to set topic");
      return;
    }

    Optional<JDA> jdaOpt = jdaMaker.getJda();
    List<TextChannel> textChannels = new ArrayList<>();

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();

      for (Long textChannelId : textChannelIds) {
        TextChannel textChannel = jda.getTextChannelById(textChannelId);
        textChannels.add(textChannel);
      }

      log.debug("Get text channels by IDs");
    } else {
      log.error("Failed to set topic: JDA instance not available");
    }

    for (TextChannel textChannel : textChannels) {
      textChannel.getManager().setTopic(topic).queue();
    }
  }
}
