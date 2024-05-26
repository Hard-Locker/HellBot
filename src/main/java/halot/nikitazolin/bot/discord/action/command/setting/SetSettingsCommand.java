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
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetSettingsCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "set";
  private final String close = "close";
  private final String aloneTime = "aloneTime";
  private final String songInStatus = "songInStatus";
  private final String stayInChannel = "stayInChannel";
  private final String updateAlerts = "updateAlerts";
//  private final String allowedTextChannelIds = "allowedTextChannelIds";
//  private final String allowedVoiceChannelIds = "allowedVoiceChannelIds";
//  private final String playlistFolderPaths = "playlistFolderPaths";
//  private final String prefixes = "prefixes";
//  private final String nameAliases = "nameAliases";

  private Map<String, Consumer<ButtonInteractionEvent>> buttonHandlers = new HashMap<>();
  private Map<String, Consumer<ModalInteractionEvent>> modalHandlers = new HashMap<>();

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
    return "You can change any settings";
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
    Button aloneTimeButton = Button.primary(aloneTime, "Set alone time");
    Button songInStatusButton = Button.primary(songInStatus, "Set song in status");
    Button stayInChannelButton = Button.primary(stayInChannel, "Set stay in channel");
    Button updateAlertsButton = Button.primary(updateAlerts, "Set update alerts");
//    Button allowedTextChannelIdsButton = Button.primary(allowedTextChannelIds, "Set allowed text channel");
//    Button allowedVoiceChannelIdsButton = Button.primary(allowedVoiceChannelIds, "Set allowed voice channel");
//    Button playlistFolderPathsButton = Button.primary(playlistFolderPaths, "Set playlist folders");
//    Button prefixesButton = Button.primary(prefixes, "Set prefixes");
//    Button nameAliasesButton = Button.primary(nameAliases, "Set name aliases");
    List<Button> buttons = List.of(closeButton, aloneTimeButton, songInStatusButton, stayInChannelButton,
        updateAlertsButton);

    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), "Which setting need update?",
        buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(aloneTime, this::makeModalAloneTime);
//    buttonHandlers.put(songInStatus, this::make);
//    buttonHandlers.put(stayInChannel, this::make);
//    buttonHandlers.put(updateAlerts, this::make);
//    buttonHandlers.put(allowedTextChannelIds, this::make);
//    buttonHandlers.put(allowedVoiceChannelIds, this::make);
//    buttonHandlers.put(playlistFolderPaths, this::make);
//    buttonHandlers.put(prefixes, this::make);
//    buttonHandlers.put(nameAliases, this::make);

    modalHandlers.put(aloneTime, this::handleModalAloneTime);
//    modalHandlers.put(songInStatus, this::set);
//    modalHandlers.put(stayInChannel, this::set);
//    modalHandlers.put(updateAlerts, this::set);
//    modalHandlers.put(allowedTextChannelIds, this::set);
//    modalHandlers.put(allowedVoiceChannelIds, this::set);
//    modalHandlers.put(playlistFolderPaths, this::set);
//    modalHandlers.put(prefixes, this::set);
//    modalHandlers.put(nameAliases, this::set);

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
    if (checkUserAccess(modalEvent.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(modalEvent.getUser());
      log.debug("User {} does not have access to use: {}", modalEvent.getUser(), commandName);

      return;
    }

    String modalId = modalEvent.getModalId();
    modalHandlers.getOrDefault(modalId, this::handleUnknownModal).accept(modalEvent);
  }

  private void makeModalAloneTime(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(aloneTime, "Set alone time in seconds until stop bot")
        .addActionRow(
            TextInput.create(aloneTime, "Time (seconds 0-99999)", TextInputStyle.SHORT).setRequiredRange(0, 5).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", aloneTime);
  }

  private void handleModalAloneTime(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", aloneTime);
    String inputTime = modalEvent.getValue(aloneTime).getAsString();

    try {
      Long time = Long.parseLong(inputTime);

      settings.setAloneTimeUntilStop(time);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      modalEvent.reply("Alone time set to " + time + " seconds").setEphemeral(true).queue();
      log.debug("User changed alone time to {} seconds", time);
    } catch (NumberFormatException e) {
      log.debug("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for user ID", e);
    }
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

  private void handleUnknownModal(ModalInteractionEvent modalEvent) {
    modalEvent.reply("Unknown modal").setEphemeral(true).queue();
    log.debug("Clicked modal button");
  }
}
