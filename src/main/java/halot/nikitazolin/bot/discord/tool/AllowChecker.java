package halot.nikitazolin.bot.discord.tool;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

@Component
@Slf4j
@RequiredArgsConstructor
public class AllowChecker {

  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;

  public boolean isAllowedTextChannel(TextChannel textChannel, User user) {
    List<Long> allowedTextChannelIds = new ArrayList<>();

    try {
      allowedTextChannelIds.addAll(settings.getAllowedTextChannelIds());
    } catch (NullPointerException e) {
      log.debug("Especial allowed text channel not set. Bot can connect read any channel.");

      return true;
    }

    if (allowedTextChannelIds.isEmpty() || allowedTextChannelIds.contains(textChannel.getIdLong())) {
      return true;
    } else {
      EmbedBuilder embed = messageFormatter.createErrorEmbed(textChannel.getAsMention() + " is denied for bot");
      messageSender.sendPrivateMessage(user, embed);
      log.debug("User tried to use a command in an denied text channel");

      return false;
    }
  }

  public boolean isOwnerOrAdminOrDj(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getOwnerUserId() != null) {
      allowedIds.add(settings.getOwnerUserId());
    }

    if (settings.getAdminUserIds() != null) {
      allowedIds.addAll(settings.getAdminUserIds());
    }

    if (settings.getDjUserIds() != null) {
      allowedIds.addAll(settings.getDjUserIds());
    }

    if (allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isOwnerOrAdmin(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getOwnerUserId() != null) {
      allowedIds.add(settings.getOwnerUserId());
    }

    if (settings.getAdminUserIds() != null) {
      allowedIds.addAll(settings.getAdminUserIds());
    }

    if (allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isAdmin(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getAdminUserIds() != null) {
      allowedIds.addAll(settings.getAdminUserIds());
    }

    if (allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isOwner(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getOwnerUserId() != null) {
      allowedIds.add(settings.getOwnerUserId());
    }

    if (allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isDj(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getDjUserIds() != null) {
      allowedIds.addAll(settings.getDjUserIds());
    }

    if (allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isNotBanned(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getBannedUserIds() != null) {
      allowedIds.addAll(settings.getBannedUserIds());
    }

    if (!allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
  }

  //TODO need change checking logic. Use isAfkChannel instead of checkAfkChannel and etc.
  public boolean isAllowedVoiceChannel(BotCommandContext context, VoiceChannel userVoiceChannel, Settings settings) {
    if (!checkAfkChannel(context, userVoiceChannel)) {
      return false;
    }

    if (!checkSameChannel(context, userVoiceChannel)) {
      return false;
    }

    if (!checkAllowedVoiceChannel(context, userVoiceChannel, settings)) {
      return false;
    }

    return true;
  }

  private boolean checkAfkChannel(BotCommandContext context, VoiceChannel userVoiceChannel) {
    VoiceChannel afkVoiceChannel = context.getGuild().getAfkChannel();

    if (afkVoiceChannel != null && afkVoiceChannel.equals(userVoiceChannel)) {
      EmbedBuilder embed = messageFormatter
          .createErrorEmbed(context.getUser().getAsMention() + ", this command cannot be used in the AFK channel.");
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
      log.debug("User try call bot in AFK channel. User: " + context.getUser());

      return false;
    } else {
      return true;
    }
  }

  private boolean checkSameChannel(BotCommandContext context, VoiceChannel userVoiceChannel) {
    VoiceChannel botVoiceChannel = null;

    try {
      botVoiceChannel = context.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel();
    } catch (NullPointerException e) {
      log.debug("The bot is not in the voice channel");
    }

    if (botVoiceChannel != null && !botVoiceChannel.equals(userVoiceChannel)) {
      EmbedBuilder embed = messageFormatter.createErrorEmbed(
          context.getUser().getAsMention() + ", you must be in the same voice channel as the bot to use this command.");
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
      log.debug("User try call bot in different channel. User:" + context.getUser());

      return false;
    } else {
      return true;
    }
  }

  private boolean checkAllowedVoiceChannel(BotCommandContext context, VoiceChannel userVoiceChannel,
      Settings settings) {
    List<Long> allowedVoiceChannelIds = new ArrayList<>();

    try {
      allowedVoiceChannelIds.addAll(settings.getAllowedVoiceChannelIds());
    } catch (NullPointerException e) {
      log.debug("Especial allowed voice channel not set. Bot can connect to any channel.");
      return true;
    }

    if (allowedVoiceChannelIds.isEmpty() || allowedVoiceChannelIds.contains(userVoiceChannel.getIdLong())) {
      return true;
    } else {
      EmbedBuilder embed = messageFormatter
          .createErrorEmbed(context.getUser().getAsMention() + ", your voice channel is denied for bot.");
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
      log.debug("User tried to use a command in an denied voice channel.");

      return false;
    }
  }
}
