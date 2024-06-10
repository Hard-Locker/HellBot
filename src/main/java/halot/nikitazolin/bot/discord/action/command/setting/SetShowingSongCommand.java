package halot.nikitazolin.bot.discord.action.command.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.ApplicationRunnerImpl;
import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.command.setting.SettingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetShowingSongCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;
  private final SettingProvider settingProvider;

  private final String commandName = "song";
  private final String close = "close";
  private final String songInActivity = "songInActivity";
  private final String songInTopic = "songInTopic";
  private final String songInTextChannel = "songInTextChannel";

  private Map<String, Consumer<ButtonInteractionEvent>> buttonHandlers = new HashMap<>();

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
    return settingProvider.getText("set_showing_song_command.description");
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

    Button closeButton = Button.danger(close, settingProvider.getText("setting.button.close"));
    Button songInActivityButton = Button.primary(songInActivity,
        settingProvider.getText("set_showing_song_command.button.activity"));
    Button songInTopicButton = Button.primary(songInTopic,
        settingProvider.getText("set_showing_song_command.button.topic"));
    Button songInTextChannelButton = Button.primary(songInTextChannel,
        settingProvider.getText("set_showing_song_command.button.text_channel"));
    List<Button> buttons = List.of(closeButton, songInActivityButton, songInTopicButton, songInTextChannelButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder();
    messageContent.append("**" + settingProvider.getText("set_showing_song_command.message.title") + "**");
    messageContent.append(newLine);

    messageContent.append(settingProvider.getText("set_showing_song_command.message.current_activity") + ": ");
    messageContent.append(settings.isSongInActivity() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no"));
    messageContent.append(newLine);

    messageContent.append(settingProvider.getText("set_showing_song_command.message.current_topic") + ": ");
    messageContent.append(settings.isSongInTopic() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no"));
    messageContent.append(newLine);

    messageContent.append(settingProvider.getText("set_showing_song_command.message.current_text_channel") + ": ");
    messageContent.append(settings.isSongInTextChannel() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no"));
    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(songInActivity, this::handleButtonSongInActivity);
    buttonHandlers.put(songInTopic, this::handleButtonSongInTopic);
    buttonHandlers.put(songInTextChannel, this::handleButtonSongInTextChannel);

    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName, 300000));
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    if (checkUserAccess(buttonEvent.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(buttonEvent.getUser());
      log.debug("User {} does not have access to use: {}", buttonEvent.getUser(), commandName);

      return;
    }

    String componentId = buttonEvent.getComponentId();
    buttonHandlers.getOrDefault(componentId, this::handleUnknownButton).accept(buttonEvent);
  }

  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    return;
  }

  private void handleButtonSongInActivity(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", songInActivity);

    if (settings.isSongInActivity() == true) {
      settings.setSongInActivity(false);
    } else {
      settings.setSongInActivity(true);
    }

    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
    String info = settings.isSongInActivity() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no");
    buttonEvent.reply(settingProvider.getText("set_showing_song_command.message.set_activity") + ": " + info)
        .setEphemeral(true).queue();
  }

  private void handleButtonSongInTopic(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", songInTopic);

    if (settings.isSongInTopic() == true) {
      settings.setSongInTopic(false);
    } else {
      settings.setSongInTopic(true);
    }

    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
    String info = settings.isSongInTopic() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no");
    buttonEvent.reply(settingProvider.getText("set_showing_song_command.message.set_topic") + ": " + info)
        .setEphemeral(true).queue();
  }

  private void handleButtonSongInTextChannel(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", songInTextChannel);

    if (settings.isSongInTextChannel() == true) {
      settings.setSongInTextChannel(false);
    } else {
      settings.setSongInTextChannel(true);
    }

    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
    String info = settings.isSongInTextChannel() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no");
    buttonEvent.reply(settingProvider.getText("set_showing_song_command.message.set_text_channel") + ": " + info)
        .setEphemeral(true).queue();
  }

  private void selectClose(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply(settingProvider.getText("setting.message.close")).setEphemeral(true).queue();
    buttonEvent.getMessage().delete().queue();
    log.debug("Settings closed");
  }

  private void handleUnknownButton(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply(settingProvider.getText("setting.message.button.unknown")).setEphemeral(true).queue();
    log.debug("Clicked unknown button");
  }
}
