package halot.nikitazolin.bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import halot.nikitazolin.bot.HellBot;

import java.awt.*;
import java.time.Instant;

public final class MessageUtil {

  private static final Color BOT_COLOR_SUCCESS = new Color(88, 170, 137);
  private static final Color BOT_COLOR_ERROR = new Color(191, 61, 39);
  private static final Color BOT_COLOR_INFO = new Color(32, 102, 148);
  private static final Color BOT_COLOR_INFO_ALT = new Color(87, 97, 133);
  private static final Color BOT_COLOR_WARNING = new Color(255, 122, 0);

  private MessageUtil() {
    throw new AssertionError("No, bad! No instances of util classes!");
  }

  /**
   * Creates a success embed with the usual format used by the bot
   *
   * @return {@link EmbedBuilder}
   */
  public static EmbedBuilder createSuccessEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_SUCCESS);
    return embedBuilder;
  }

  /**
   * Creates a success embed with usual format used by the bot, including the
   * message in the embed
   *
   * @return {@link EmbedBuilder}
   */
  public static EmbedBuilder createSuccessEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_SUCCESS);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  /**
   * Creates a error embed with the usual format used by the bot
   *
   * @return {@link EmbedBuilder}
   */
  public static EmbedBuilder createErrorEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_ERROR);
    return embedBuilder;
  }

  /**
   * Creates a error embed with usual format used by the bot, including the
   * message in the embed
   *
   * @return {@link EmbedBuilder}
   */
  public static EmbedBuilder createErrorEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_ERROR);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  /**
   * Creates a info embed with the usual format used by the bot
   *
   * @return {@link EmbedBuilder}
   */
  public static EmbedBuilder createInfoEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    return embedBuilder;
  }

  /**
   * Creates a info embed with usual format used by the bot, including the message
   * in the embed
   *
   * @return {@link EmbedBuilder}
   */
  public static EmbedBuilder createInfoEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  /**
   * Creates an info embed with usual format used by the bot, including the
   * message and the title in the embed
   *
   * @param message message the embed shall contain
   * @param title   title of the embed
   * @return {@link EmbedBuilder}
   */
  public static EmbedBuilder createInfoEmbedWithTitle(String message, String title) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    embedBuilder.setTitle(title);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  /**
   * Creates a alt info embed with the usual format used by the bot
   *
   * @return Embed
   */
  public static EmbedBuilder createAltInfoEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO_ALT);
    return embedBuilder;
  }

  /**
   * Creates an alt info embed with usual format used by the bot, including the
   * message in the embed
   *
   * @return Embed
   */
  public static EmbedBuilder createAltInfoEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO_ALT);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  /**
   * Creates a warning info embed with the usual format used by the bot
   *
   * @return Embed
   */
  public static EmbedBuilder createWarningEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_WARNING);
    return embedBuilder;
  }

  /**
   * Creates a warning embed with usual format used by the bot, including the
   * message in the embed
   *
   * @return Embed
   */
  public static EmbedBuilder createWarningEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_WARNING);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  /**
   * Sets the message for an embed
   *
   * @param embedBuilder embed you want to set the message for
   * @param message      the message to set
   */
  public static void setEmbedMessage(EmbedBuilder embedBuilder, String message) {
    embedBuilder.setDescription(message);
  }

  private static void setCommonFooter(EmbedBuilder embedBuilder) {
    embedBuilder.setFooter(HellBot.getJdaService().getJda().get().getSelfUser().getName(),
        HellBot.getJdaService().getJda().get().getSelfUser().getAvatarUrl());
  }

  private static void setTimestamp(EmbedBuilder embedBuilder) {
    embedBuilder.setTimestamp(Instant.now());
  }
}
