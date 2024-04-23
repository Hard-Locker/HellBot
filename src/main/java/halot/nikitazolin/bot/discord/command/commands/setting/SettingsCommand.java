package halot.nikitazolin.bot.discord.command.commands.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.command.BotCommandContext;
import halot.nikitazolin.bot.discord.command.model.BotCommand;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SettingsCommand extends BotCommand {

  private final MessageUtil messageUtil;
  private final Settings settings;

  private final String commandName = "settings";

  @Override
  public String name() {
    return commandName;
  }

  @Override
  public List<String> nameAliases() {
    List<String> defaultAliases = List.of(commandName);
    List<String> additionalAliases = settings.getNameAliases().getOrDefault(commandName, List.of());
    List<String> allAliases = new ArrayList<>(defaultAliases);
    allAliases.addAll(additionalAliases);

    return allAliases;
  }

  @Override
  public List<String> commandPrefixes() {
    List<String> prefixes = new ArrayList<>(List.of("!"));
    List<String> additionalPrefixes = settings.getPrefixes() != null ? settings.getPrefixes() : List.of();
    prefixes.addAll(additionalPrefixes);

    return prefixes;
  }

  @Override
  public String description() {
    return "Show all settings";
  }

  @Override
  public String requiredRole() {
    return null;
  }

  @Override
  public Permission neededPermission() {
    return Permission.USE_APPLICATION_COMMANDS;
  }

  @Override
  public boolean guildOnly() {
    return true;
  }

  @Override
  public OptionData[] options() {
    return new OptionData[] {};
  }

  @Override
  public void execute(BotCommandContext context) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getOwnerUserId() != null) {
      allowedIds.add(settings.getOwnerUserId());
    }

    if (settings.getAdminUserIds() != null) {
      allowedIds.addAll(settings.getAdminUserIds());
    }

    if (allowedIds.contains(context.getUser().getIdLong())) {
      EmbedBuilder embed = messageUtil.createAltInfoEmbed("Сurrent bot settings:");
      String notSetValue = "Not set";

      embed.addField("Volume", String.valueOf(settings.getVolume()), true);
      embed.addField("Owner ID", settings.getOwnerUserId() != null ? settings.getOwnerUserId().toString() : notSetValue, true);
      embed.addField("Alone time until stop", settings.getAloneTimeUntilStop() != null ? settings.getAloneTimeUntilStop().toString() : notSetValue, true);
      embed.addField("Bot status at start", settings.getBotStatusAtStart() != null ? settings.getBotStatusAtStart() : notSetValue, true);
      embed.addField("Bot activity at start", settings.getBotActivityAtStart() != null ? settings.getBotActivityAtStart() : notSetValue, true);
      embed.addField("Song in status", String.valueOf(settings.isSongInStatus()), true);
      embed.addField("Stay in channel", String.valueOf(settings.isStayInChannel()), true);
      embed.addField("Update alerts", String.valueOf(settings.isUpdateAlerts()), true);
      embed.addField("Allowed text channel IDs", settings.getAllowedTextChannelIds() != null ? settings.getAllowedTextChannelIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
      embed.addField("Allowed voice channel IDs", settings.getAllowedVoiceChannelIds() != null ? settings.getAllowedVoiceChannelIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
      embed.addField("Admin IDs", settings.getAdminUserIds() != null ? settings.getAdminUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
      embed.addField("DJ IDs", settings.getDjUserIds() != null ? settings.getDjUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
      embed.addField("Banned IDs", settings.getBannedUserIds() != null ? settings.getBannedUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
      embed.addField("Playlist folder paths", settings.getPlaylistFolderPaths() != null ? String.join(", ", settings.getPlaylistFolderPaths()) : notSetValue, false);
      embed.addField("Prefixes", settings.getPrefixes() != null ? String.join(", ", settings.getPrefixes()) : notSetValue, false);

      if (settings.getNameAliases() != null && !settings.getNameAliases().isEmpty()) {
        StringBuilder aliasesBuilder = new StringBuilder();
        settings.getNameAliases().forEach((key, value) -> aliasesBuilder.append(key).append(": ").append(String.join(", ", value)).append("\n"));
        embed.addField("Name aliases", aliasesBuilder.toString(), false);
      } else {
        embed.addField("Name aliases", notSetValue, false);
      }

      context.sendPrivateMessage(embed);
      log.debug("User show settings" + context.getUser());
    } else {
      EmbedBuilder embed = messageUtil.createAltInfoEmbed("You have not permission for use this command");
      context.sendPrivateMessage(embed);
      log.debug("User have not permission for show settings" + context.getUser());
    }
  }
}
