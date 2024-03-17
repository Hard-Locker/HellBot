package halot.nikitazolin.bot.util;

import java.awt.Color;
import java.time.Instant;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.jda.JdaService;
import net.dv8tion.jda.api.EmbedBuilder;

@Component
@Lazy
@Scope("prototype")
public class MessageUtil {

  private final JdaService jdaService;

  public MessageUtil(JdaService jdaService) {
    this.jdaService = jdaService;
  }

  private static final Color BOT_COLOR_SUCCESS = new Color(88, 170, 137);
  private static final Color BOT_COLOR_ERROR = new Color(191, 61, 39);
  private static final Color BOT_COLOR_INFO = new Color(32, 102, 148);
  private static final Color BOT_COLOR_INFO_ALT = new Color(87, 97, 133);
  private static final Color BOT_COLOR_WARNING = new Color(255, 122, 0);

//  private MessageUtil() {
//    throw new AssertionError("No, bad! No instances of util classes!");
//  }

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
    System.out.println("setCommonFooter, jdaService null?" + (jdaService.getJda() == null));
    
    embedBuilder.setFooter(jdaService.getJda().get().getSelfUser().getName(), jdaService.getJda().get().getSelfUser().getAvatarUrl());
//    System.out.println("setCommonFooter, jdaService null?" + (HellBot.getJdaService().getJda() == null));
//    
//    embedBuilder.setFooter(HellBot.getJdaService().getJda().get().getSelfUser().getName(),
//        HellBot.getJdaService().getJda().get().getSelfUser().getAvatarUrl());
  }

  private void setTimestamp(EmbedBuilder embedBuilder) {
    embedBuilder.setTimestamp(Instant.now());
  }
}
