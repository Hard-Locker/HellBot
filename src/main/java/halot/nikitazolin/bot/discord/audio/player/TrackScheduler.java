package halot.nikitazolin.bot.discord.audio.player;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.tool.ActivityManager;
import halot.nikitazolin.bot.discord.tool.DiscordDataReceiver;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.command.music.MusicProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class TrackScheduler extends AudioEventAdapter implements AudioEventListener {

  private final GuildAudioService guildAudioService;
  private final PlayerService playerService;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final ActivityManager activityManager;
  private final DiscordDataReceiver discordDataReceiver;
  private final MusicProvider musicProvider;

//  private boolean isRepeat = false;

  @Override
  public void onEvent(AudioEvent event) {
    super.onEvent(event);
  }

  @Override
  public void onPlayerPause(AudioPlayer player) {
    String info = musicProvider.getText("info.now_paused");

    if (settings.isSongInActivity() == true) {
      activityManager.setCustomStatus(info);
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<TextChannel> textChannels = discordDataReceiver.getTextChannelsByIds(settings.getAllowedTextChannelIds());
      setTopics(textChannels, info);
    }
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
    AudioTrack audioTrack = playerService.getAudioPlayer().getPlayingTrack();
    String song = audioTrack.getInfo().author + " - " + audioTrack.getInfo().title;
    String topic = musicProvider.getText("info.now_playing") + ": " + song;

    if (settings.isSongInActivity() == true) {
      activityManager.setListening(song);
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<TextChannel> textChannels = discordDataReceiver.getTextChannelsByIds(settings.getAllowedTextChannelIds());
      setTopics(textChannels, topic);
    }
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
    AudioTrackInfo audioTrackInfo = playerService.getAudioPlayer().getPlayingTrack().getInfo();
    String song = audioTrackInfo.author + " - " + audioTrackInfo.title;
    String topic = musicProvider.getText("info.now_playing") + ": " + song;

    if (settings.isSongInActivity() == true) {
      activityManager.setListening(song);
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<TextChannel> textChannels = discordDataReceiver.getTextChannelsByIds(settings.getAllowedTextChannelIds());
      setTopics(textChannels, topic);
    }

    if (settings.isSongInTextChannel() == true && settings.getAllowedTextChannelIds() != null) {
      EmbedBuilder embed = messageFormatter.createAudioTrackInfoEmbed(audioTrackInfo,
          ("**" + musicProvider.getText("info.now_playing") + "**"));
      List<TextChannel> textChannels = discordDataReceiver.getTextChannelsByIds(settings.getAllowedTextChannelIds());

      for (TextChannel textChannel : textChannels) {
        messageSender.sendMessageEmbed(textChannel, embed);
      }

      log.debug("Audio information send to text channel");
    }
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (settings.isSongInActivity() == true && playerService.getQueue().isEmpty() == true) {
      activityManager.setCustomStatus(musicProvider.getText("info.now_chill"));
    }

    if (settings.isSongInTopic() == true && settings.getAllowedTextChannelIds() != null) {
      List<TextChannel> textChannels = discordDataReceiver.getTextChannelsByIds(settings.getAllowedTextChannelIds());
      setTopics(textChannels, "");
    }

    if (playerService.getQueue().isEmpty() == true && settings.isStayInChannel() == false) {
      guildAudioService.stopAudioSending();
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

  private void setTopics(List<TextChannel> textChannels, String topic) {
    log.debug("Updating text channels topic");

    if (textChannels.isEmpty() == true) {
      log.debug("Have not text channels to set topic");
      return;
    }

    for (TextChannel textChannel : textChannels) {
      textChannel.getManager().setTopic(topic).queue();
    }
  }
}
