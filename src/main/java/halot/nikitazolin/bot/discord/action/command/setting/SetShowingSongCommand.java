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

  private final String commandName = "song";
  private final String close = "close";
  private final String songInStatus = "songInStatus";
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
    return "Change settings showing current song";
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

    Button closeButton = Button.danger(close, "Close settings");
    Button songInStatusButton = Button.primary(songInStatus, "Set song in status");
    Button songInTopicButton = Button.primary(songInTopic, "Set song in topic");
    Button songInTextChannelButton = Button.primary(songInTextChannel, "Set song in text channel");
    List<Button> buttons = List.of(closeButton, songInStatusButton, songInTopicButton, songInTextChannelButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder("**Settings showing song**").append(newLine);

    messageContent.append("Show song in status: ");
    messageContent.append(settings.isSongInStatus() == true ? "Yes" : "No");
    messageContent.append(newLine);

    messageContent.append("Show song in topic: ");
    messageContent.append(settings.isSongInTopic() == true ? "Yes" : "No");
    messageContent.append(newLine);

    messageContent.append("Show song in text channel: ");
    messageContent.append(settings.isSongInTextChannel() == true ? "Yes" : "No");
    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(songInStatus, this::handleButtonSongInStatus);
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

  private void handleButtonSongInStatus(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", songInStatus);

    if (settings.isSongInStatus() == true) {
      settings.setSongInStatus(false);
    } else {
      settings.setSongInStatus(true);
    }

    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
  }

  private void handleButtonSongInTopic(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", songInTopic);

    if (settings.isSongInTopic() == true) {
      settings.setSongInTopic(false);
    } else {
      settings.setSongInTopic(true);
    }

    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
  }

  private void handleButtonSongInTextChannel(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", songInTextChannel);

    if (settings.isSongInTextChannel() == true) {
      settings.setSongInTextChannel(false);
    } else {
      settings.setSongInTextChannel(true);
    }

    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
  }

  private void selectClose(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply("Settings closed").setEphemeral(true).queue();
    buttonEvent.getMessage().delete().queue();
    log.debug("Settings closed");
  }

  private void handleUnknownButton(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply("Unknown button").setEphemeral(true).queue();
    log.debug("Clicked unknown button");
  }
}
