package halot.nikitazolin.bot.discord.tool;

import java.awt.Color;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.util.TimeConverter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

@Component
@RequiredArgsConstructor
public class MessageFormatter {

  private final JdaMaker jdaMaker;
  private final TimeConverter timeConverter;

  private static final Color BOT_COLOR_SUCCESS = new Color(0, 255, 0);
  private static final Color BOT_COLOR_ERROR = new Color(255, 0, 0);
  private static final Color BOT_COLOR_INFO = new Color(255, 255, 0);
  private static final Color BOT_COLOR_INFO_ALT = new Color(0, 255, 255);
  private static final Color BOT_COLOR_WARNING = new Color(255, 69, 0);

  public EmbedBuilder createSuccessEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_SUCCESS);
    return embedBuilder;
  }

  public EmbedBuilder createSuccessEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_SUCCESS);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public EmbedBuilder createErrorEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_ERROR);
    return embedBuilder;
  }

  public EmbedBuilder createErrorEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_ERROR);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public EmbedBuilder createInfoEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    return embedBuilder;
  }

  public EmbedBuilder createInfoEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public EmbedBuilder createInfoEmbedWithTitle(String message, String title) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    embedBuilder.setTitle(title);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public EmbedBuilder createAltInfoEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO_ALT);
    return embedBuilder;
  }

  public EmbedBuilder createAltInfoEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO_ALT);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public EmbedBuilder createWarningEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_WARNING);
    return embedBuilder;
  }

  public EmbedBuilder createWarningEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_WARNING);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public EmbedBuilder createAudioTrackInfoEmbed(AudioTrackInfo audioTrackInfo, String title) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO_ALT);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setTitle(title);

    embedBuilder.setThumbnail(audioTrackInfo.artworkUrl);
    embedBuilder.addField("Name", audioTrackInfo.title, false);
    embedBuilder.addField("Author", audioTrackInfo.author, false);
    embedBuilder.addField("URL", audioTrackInfo.uri, false);
    embedBuilder.addField("Duration", timeConverter.convertLongTimeToSimpleFormat(audioTrackInfo.length), false);
    return embedBuilder;
  }

  private void setCommonFooter(EmbedBuilder embedBuilder) {
    jdaMaker.getJda().ifPresent(jda -> {
      String name = jda.getSelfUser().getName();
      String avatarUrl = jda.getSelfUser().getAvatarUrl();
      embedBuilder.setFooter(name, avatarUrl);
    });
  }

  private void setTimestamp(EmbedBuilder embedBuilder) {
    embedBuilder.setTimestamp(Instant.now());
  }
}
