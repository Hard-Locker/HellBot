package halot.nikitazolin.bot.discord.action.command.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.DiscordDataReceiver;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.command.setting.SettingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
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
  private final DiscordDataReceiver discordDataReceiver;
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
    return settingProvider.getText("show_settings.description");
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

    messageSender.sendPrivateMessage(context.getUser(), makeMessage());
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

  @Override
  public void stringSelectProcessing(StringSelectInteractionEvent stringSelectEvent) {
    return;
  }

  private EmbedBuilder makeMessage() {
    List<String> allowedTextChannels = getAllowedTextChannelMentions();
    List<String> allowedVoiceChannels = getAllowedVoiceChannelMentions();
    List<String> admins = getAdminUserMentions();
    List<String> djs = getDjUserMentions();
    List<String> bans = getBannedUserMentions();

    String newLine = System.lineSeparator();
    String localYes = settingProvider.getText("setting.text.yes");
    String localNo = settingProvider.getText("setting.text.no");
    String localNotSet = settingProvider.getText("setting.text.not_set");
    String localLanguage = settingProvider.getText("show_settings.message.language");
    String localVolume = settingProvider.getText("show_settings.message.volume");
    String localOwner = settingProvider.getText("show_settings.message.owner");
    String localStatus = settingProvider.getText("show_settings.message.status");
    String localSongInActivity = settingProvider.getText("show_settings.message.songInActivity");
    String localSongInTopic = settingProvider.getText("show_settings.message.songInTopic");
    String localSongInTextChannel = settingProvider.getText("show_settings.message.songInTextChannel");
    String localStayInChannel = settingProvider.getText("show_settings.message.stayInChannel");
    String localAloneTime = settingProvider.getText("show_settings.message.aloneTime");
    String localUpdate = settingProvider.getText("show_settings.message.update");
    String localAllowedTextChannel = settingProvider.getText("show_settings.message.allowedTextChannel");
    String localAllowedVoiceChannel = settingProvider.getText("show_settings.message.allowedVoiceChannel");
    String localAdmins = settingProvider.getText("show_settings.message.admins");
    String localDjs = settingProvider.getText("show_settings.message.djs");
    String localBan = settingProvider.getText("show_settings.message.ban");
    String localPlaylists = settingProvider.getText("show_settings.message.playlists");
    String localPrefixes = settingProvider.getText("show_settings.message.prefixes");
    String localNameAliases = settingProvider.getText("show_settings.message.nameAliases");
    String localMessageTitle = settingProvider.getText("show_settings.message.title");

    EmbedBuilder embed = messageFormatter.createAltInfoEmbed(localMessageTitle + ":");

    String language = String.valueOf(settings.getLanguage());
    String volume = String.valueOf(settings.getVolume());
    String ownerUser = settings.getOwnerUserId() != null ? discordDataReceiver.getUserById(settings.getOwnerUserId()).getAsMention() : localNotSet;
    String botStatus = settings.getBotStatus() != null ? settings.getBotStatus() : localNotSet;
    String songInActivity = settings.isSongInActivity() == true ? localYes : localNo;
    String songInTopic = settings.isSongInTopic() == true ? localYes : localNo;
    String songInTextChannel = settings.isSongInTextChannel() == true ? localYes : localNo;
    String stayInChannel = settings.isStayInChannel() == true ? localYes : localNo;
    String aloneTimeUntilStop = settings.getAloneTimeUntilStop() == 0 ? localNotSet : String.valueOf(settings.getAloneTimeUntilStop());
    String updateNotification = settings.isUpdateNotification() == true ? localYes : localNo;
    String lineAllowedTextChannels = allowedTextChannels.isEmpty() == false ? allowedTextChannels.stream().collect(Collectors.joining(", ")) : localNotSet;
    String lineAllowedVoiceChannels = allowedVoiceChannels.isEmpty() == false ? allowedVoiceChannels.stream().collect(Collectors.joining(", ")) : localNotSet;
    String lineAdminUsers = admins.isEmpty() == false ? admins.stream().collect(Collectors.joining(", ")) : localNotSet;
    String lineDjUsers = djs.isEmpty() == false ? djs.stream().collect(Collectors.joining(", ")) : localNotSet;
    String lineBannedUsers = bans.isEmpty() == false ? bans.stream().collect(Collectors.joining(", ")) : localNotSet;
    String prefixes = settings.getPrefixes() != null ? String.join(", ", settings.getPrefixes()) : localNotSet;
    String playlistsData = getPlaylistsData(newLine, localNotSet);
    String nameAliasesData = getNameAliasesData(newLine, localNotSet);

    embed.addField(localLanguage, language, true);
    embed.addField(localVolume, volume, true);
    embed.addField(localOwner, ownerUser, true);
    embed.addField(localStatus, botStatus, true);
    embed.addField(localSongInActivity, songInActivity, true);
    embed.addField(localSongInTopic, songInTopic, true);
    embed.addField(localSongInTextChannel, songInTextChannel, true);
    embed.addField(localStayInChannel, stayInChannel, true);
    embed.addField(localAloneTime, aloneTimeUntilStop, true);
    embed.addField(localUpdate, updateNotification, true);
    embed.addField(localAllowedTextChannel, lineAllowedTextChannels, false);
    embed.addField(localAllowedVoiceChannel, lineAllowedVoiceChannels, false);
    embed.addField(localAdmins, lineAdminUsers, false);
    embed.addField(localDjs, lineDjUsers, false);
    embed.addField(localBan, lineBannedUsers, false);
    embed.addField(localPrefixes, prefixes, false);
    embed.addField(localPlaylists, playlistsData, false);
    embed.addField(localNameAliases, nameAliasesData, false);

    return embed;
  }

  private List<String> getAllowedTextChannelMentions() {
    if (settings.getAllowedTextChannelIds() != null && !settings.getAllowedTextChannelIds().isEmpty()) {
      List<TextChannel> textChannels = discordDataReceiver.getTextChannelsByIds(settings.getAllowedTextChannelIds());
      return textChannels.stream().map(TextChannel::getAsMention).toList();
    }

    return new ArrayList<>();
  }

  private List<String> getAllowedVoiceChannelMentions() {
    if (settings.getAllowedVoiceChannelIds() != null && !settings.getAllowedVoiceChannelIds().isEmpty()) {
      List<VoiceChannel> textChannels = discordDataReceiver.getVoiceChannelsByIds(settings.getAllowedVoiceChannelIds());
      return textChannels.stream().map(VoiceChannel::getAsMention).toList();
    }

    return new ArrayList<>();
  }

  private List<String> getDjUserMentions() {
    if (settings.getDjUserIds() != null && !settings.getDjUserIds().isEmpty()) {
      List<User> users = discordDataReceiver.getUsersByIds(settings.getDjUserIds());
      return users.stream().map(User::getAsMention).toList();
    }

    return new ArrayList<>();
  }

  private List<String> getBannedUserMentions() {
    if (settings.getBannedUserIds() != null && !settings.getBannedUserIds().isEmpty()) {
      List<User> users = discordDataReceiver.getUsersByIds(settings.getBannedUserIds());
      return users.stream().map(User::getAsMention).toList();
    }

    return new ArrayList<>();
  }

  private List<String> getAdminUserMentions() {
    if (settings.getAdminUserIds() != null && !settings.getAdminUserIds().isEmpty()) {
      List<User> users = discordDataReceiver.getUsersByIds(settings.getAdminUserIds());
      return users.stream().map(User::getAsMention).toList();
    }

    return new ArrayList<>();
  }

  private String getPlaylistsData(String newLine, String localNotSet) {
    if (settings.getPlaylists() != null && !settings.getPlaylists().isEmpty()) {
      StringBuilder playlistsBuilder = new StringBuilder();

      settings.getPlaylists()
          .forEach((key, value) -> playlistsBuilder.append(key).append(": ").append(value).append(newLine));

      return playlistsBuilder.toString();
    } else {

      return localNotSet;
    }
  }

  private String getNameAliasesData(String newLine, String localNotSet) {
    if (settings.getNameAliases() != null && !settings.getNameAliases().isEmpty()) {
      StringBuilder aliasesBuilder = new StringBuilder();

      settings.getNameAliases().forEach(
          (key, value) -> aliasesBuilder.append(key).append(": ").append(String.join(", ", value)).append(newLine));

      return aliasesBuilder.toString();
    } else {

      return localNotSet;
    }
  }
}
