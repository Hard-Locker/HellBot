package halot.nikitazolin.bot.discord.action.command.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SettingsCommand extends BotCommand {

  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final AllowChecker allowChecker;

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
  public boolean checkUserAccess(User user) {
    return allowChecker.isOwnerOrAdmin(user);
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
    if (checkUserAccess(context.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(context.getUser());
      log.debug("User {} does not have access to use: {}", context.getUser(), commandName);
      
      return;
    }

    String notSetValue = "Not set";
    String newLine = System.lineSeparator();
    EmbedBuilder embed = messageFormatter.createAltInfoEmbed("Сurrent bot settings:");

    embed.addField("Language", String.valueOf(settings.getLanguage()), true);
    embed.addField("Volume", String.valueOf(settings.getVolume()), true);
    embed.addField("Owner ID", settings.getOwnerUserId() != null ? settings.getOwnerUserId().toString() : notSetValue, true);
    embed.addField("Bot status", settings.getBotStatus() != null ? settings.getBotStatus() : notSetValue, true);
    embed.addField("Song in status", String.valueOf(settings.isSongInStatus()), true);
    embed.addField("Song in topic", String.valueOf(settings.isSongInTopic()), true);
    embed.addField("Song in text channel", String.valueOf(settings.isSongInTextChannel()), true);
    embed.addField("Stay in channel", String.valueOf(settings.isStayInChannel()), true);
    embed.addField("Alone time until stop", settings.getAloneTimeUntilStop() == 0 ? notSetValue : String.valueOf(settings.getAloneTimeUntilStop()), true);
    embed.addField("Update alerts", String.valueOf(settings.isUpdateAlerts()), true);
    embed.addField("Allowed text channel IDs", settings.getAllowedTextChannelIds() != null ? settings.getAllowedTextChannelIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
    embed.addField("Allowed voice channel IDs", settings.getAllowedVoiceChannelIds() != null ? settings.getAllowedVoiceChannelIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
    embed.addField("Admin IDs", settings.getAdminUserIds() != null ? settings.getAdminUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
    embed.addField("DJ IDs", settings.getDjUserIds() != null ? settings.getDjUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
    embed.addField("Banned IDs", settings.getBannedUserIds() != null ? settings.getBannedUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSetValue, false);
    
    if (settings.getPlaylists() != null && !settings.getPlaylists().isEmpty()) {
      StringBuilder playlistsBuilder = new StringBuilder();
      settings.getPlaylists().forEach((key, value) -> playlistsBuilder.append(key).append(": ").append(value).append(newLine));
      embed.addField("Playlists", playlistsBuilder.toString(), false);
    } else {
      embed.addField("Playlists", notSetValue, false);
    }
    
    embed.addField("Prefixes", settings.getPrefixes() != null ? String.join(", ", settings.getPrefixes()) : notSetValue, false);

    if (settings.getNameAliases() != null && !settings.getNameAliases().isEmpty()) {
      StringBuilder aliasesBuilder = new StringBuilder();
      settings.getNameAliases().forEach((key, value) -> aliasesBuilder.append(key).append(": ").append(String.join(", ", value)).append(newLine));
      embed.addField("Name aliases", aliasesBuilder.toString(), false);
    } else {
      embed.addField("Name aliases", notSetValue, false);
    }

    messageSender.sendPrivateMessage(context.getUser(), embed);
    log.debug("User show settings" + context.getUser());
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    return;
  }

  @Override
  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    return;
  }
}
