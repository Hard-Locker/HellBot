package halot.nikitazolin.bot.util;

import java.awt.Color;
import java.time.Instant;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class MessageUtil {

  private final JdaMaker jdaMaker;

  private static final Color BOT_COLOR_SUCCESS = new Color(88, 170, 137);
  private static final Color BOT_COLOR_ERROR = new Color(191, 61, 39);
  private static final Color BOT_COLOR_INFO = new Color(32, 102, 148);
  private static final Color BOT_COLOR_INFO_ALT = new Color(87, 97, 133);
  private static final Color BOT_COLOR_WARNING = new Color(255, 122, 0);

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

  public void setEmbedMessage(EmbedBuilder embedBuilder, String message) {
    embedBuilder.setDescription(message);
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
