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
import halot.nikitazolin.bot.localization.action.command.setting.SettingProvider;
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
public class ShowSettingsCommand extends BotCommand {

  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final AllowChecker allowChecker;
  private final SettingProvider settingProvider;

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
    return settingProvider.getText("show_settings_command.description");
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

    String newLine = System.lineSeparator();
    String yes = settingProvider.getText("setting.text.yes");
    String no = settingProvider.getText("setting.text.no");
    String notSet = settingProvider.getText("setting.text.not_set");
    String language = settingProvider.getText("show_settings_command.message.language");
    String volume = settingProvider.getText("show_settings_command.message.volume");
    String owner = settingProvider.getText("show_settings_command.message.owner");
    String status = settingProvider.getText("show_settings_command.message.status");
    String songInActivity = settingProvider.getText("show_settings_command.message.songInActivity");
    String songInTopic = settingProvider.getText("show_settings_command.message.songInTopic");
    String songInTextChannel = settingProvider.getText("show_settings_command.message.songInTextChannel");
    String stayInChannel = settingProvider.getText("show_settings_command.message.stayInChannel");
    String aloneTime = settingProvider.getText("show_settings_command.message.aloneTime");
    String update = settingProvider.getText("show_settings_command.message.update");
    String allowedTextChannel = settingProvider.getText("show_settings_command.message.allowedTextChannel");
    String allowedVoiceChannel = settingProvider.getText("show_settings_command.message.allowedVoiceChannel");
    String admins = settingProvider.getText("show_settings_command.message.admins");
    String djs = settingProvider.getText("show_settings_command.message.djs");
    String ban = settingProvider.getText("show_settings_command.message.ban");
    String playlists = settingProvider.getText("show_settings_command.message.playlists");
    String prefixes = settingProvider.getText("show_settings_command.message.prefixes");
    String nameAliases = settingProvider.getText("show_settings_command.message.nameAliases");
    String messageTitle = settingProvider.getText("show_settings_command.message.title");

    EmbedBuilder embed = messageFormatter.createAltInfoEmbed(messageTitle + ":");

    embed.addField(language, String.valueOf(settings.getLanguage()), true);
    embed.addField(volume, String.valueOf(settings.getVolume()), true);
    embed.addField(owner + " ID", settings.getOwnerUserId() != null ? settings.getOwnerUserId().toString() : notSet, true);
    embed.addField(status, settings.getBotStatus() != null ? settings.getBotStatus() : notSet, true);
    embed.addField(songInActivity, settings.isSongInActivity() == true ? yes : no, true);
    embed.addField(songInTopic, settings.isSongInTopic() == true ? yes : no, true);
    embed.addField(songInTextChannel, settings.isSongInTextChannel() == true ? yes : no, true);
    embed.addField(stayInChannel, settings.isStayInChannel() == true ? yes : no, true);
    embed.addField(aloneTime, settings.getAloneTimeUntilStop() == 0 ? notSet : String.valueOf(settings.getAloneTimeUntilStop()), true);
    embed.addField(update, settings.isUpdateNotification() == true ? yes : no, true);
    embed.addField(allowedTextChannel + " ID", settings.getAllowedTextChannelIds() != null ? settings.getAllowedTextChannelIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSet, false);
    embed.addField(allowedVoiceChannel + " ID", settings.getAllowedVoiceChannelIds() != null ? settings.getAllowedVoiceChannelIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSet, false);
    embed.addField(admins + " ID", settings.getAdminUserIds() != null ? settings.getAdminUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSet, false);
    embed.addField(djs + " ID", settings.getDjUserIds() != null ? settings.getDjUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSet, false);
    embed.addField(ban + " ID", settings.getBannedUserIds() != null ? settings.getBannedUserIds().stream().map(Object::toString).collect(Collectors.joining(", ")) : notSet, false);

    if (settings.getPlaylists() != null && !settings.getPlaylists().isEmpty()) {
      StringBuilder playlistsBuilder = new StringBuilder();
      settings.getPlaylists().forEach((key, value) -> playlistsBuilder.append(key).append(": ").append(value).append(newLine));
      embed.addField(playlists, playlistsBuilder.toString(), false);
    } else {
      embed.addField(playlists, notSet, false);
    }

    embed.addField(prefixes, settings.getPrefixes() != null ? String.join(", ", settings.getPrefixes()) : notSet, false);

    if (settings.getNameAliases() != null && !settings.getNameAliases().isEmpty()) {
      StringBuilder aliasesBuilder = new StringBuilder();
      settings.getNameAliases().forEach((key, value) -> aliasesBuilder.append(key).append(": ").append(String.join(", ", value)).append(newLine));
      embed.addField(nameAliases, aliasesBuilder.toString(), false);
    } else {
      embed.addField(nameAliases, notSet, false);
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
