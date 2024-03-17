package halot.nikitazolin.bot.util;

import java.awt.Color;
import java.time.Instant;

import halot.nikitazolin.bot.HellBot;
import net.dv8tion.jda.api.EmbedBuilder;

public final class MessageUtil {

  private static final Color BOT_COLOR_SUCCESS = new Color(88, 170, 137);
  private static final Color BOT_COLOR_ERROR = new Color(191, 61, 39);
  private static final Color BOT_COLOR_INFO = new Color(32, 102, 148);
  private static final Color BOT_COLOR_INFO_ALT = new Color(87, 97, 133);
  private static final Color BOT_COLOR_WARNING = new Color(255, 122, 0);

  private MessageUtil() {
    throw new AssertionError("No, bad! No instances of util classes!");
  }

  public static EmbedBuilder createSuccessEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_SUCCESS);
    return embedBuilder;
  }

  public static EmbedBuilder createSuccessEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_SUCCESS);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public static EmbedBuilder createErrorEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_ERROR);
    return embedBuilder;
  }

  public static EmbedBuilder createErrorEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_ERROR);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public static EmbedBuilder createInfoEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    return embedBuilder;
  }

  public static EmbedBuilder createInfoEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public static EmbedBuilder createInfoEmbedWithTitle(String message, String title) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO);
    embedBuilder.setTitle(title);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public static EmbedBuilder createAltInfoEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO_ALT);
    return embedBuilder;
  }

  public static EmbedBuilder createAltInfoEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_INFO_ALT);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public static EmbedBuilder createWarningEmbed() {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_WARNING);
    return embedBuilder;
  }

  public static EmbedBuilder createWarningEmbed(String message) {
    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(BOT_COLOR_WARNING);
    setCommonFooter(embedBuilder);
    setTimestamp(embedBuilder);
    embedBuilder.setDescription(message);
    return embedBuilder;
  }

  public static void setEmbedMessage(EmbedBuilder embedBuilder, String message) {
    embedBuilder.setDescription(message);
  }

  private static void setCommonFooter(EmbedBuilder embedBuilder) {
    System.out.println("setCommonFooter, jdaService null?" + (HellBot.getJdaService().getJda() == null));
    
    embedBuilder.setFooter(HellBot.getJdaService().getJda().get().getSelfUser().getName(),
        HellBot.getJdaService().getJda().get().getSelfUser().getAvatarUrl());
  }

  private static void setTimestamp(EmbedBuilder embedBuilder) {
    embedBuilder.setTimestamp(Instant.now());
  }
}
