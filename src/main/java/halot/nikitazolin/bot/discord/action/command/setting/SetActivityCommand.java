package halot.nikitazolin.bot.discord.action.command.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.tool.ActivityManager;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageSender;
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
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetActivityCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final ActivityManager activityManager;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;
  private final SettingProvider settingProvider;

  private final String commandName = "activity";
  private final String close = "close";
  private final String playing = "playing";
  private final String streaming = "streaming";
  private final String listening = "listening";
  private final String watching = "watching";

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
    return settingProvider.getText("set_activity_command.description");
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
    Button playingButton = Button.primary(playing, settingProvider.getText("set_activity_command.button.playing"));
    Button streamingButton = Button.primary(streaming,
        settingProvider.getText("set_activity_command.button.streaming"));
    Button listeningButton = Button.primary(listening,
        settingProvider.getText("set_activity_command.button.listening"));
    Button watchingButton = Button.primary(watching, settingProvider.getText("set_activity_command.button.watching"));
    List<Button> buttons = List.of(closeButton, playingButton, streamingButton, listeningButton, watchingButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder();
    messageContent.append("**" + settingProvider.getText("set_activity_command.message.title") + "**");
    messageContent.append(newLine);
    messageContent.append(settingProvider.getText("set_activity_command.message.subtitle"));
    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(playing, this::makeModalPlayingActivity);
    buttonHandlers.put(streaming, this::makeModalStreamingActivity);
    buttonHandlers.put(listening, this::makeModalListeningActivity);
    buttonHandlers.put(watching, this::makeModalWatchingActivity);

    modalHandlers.put(playing, this::handleModalPlayingActivity);
    modalHandlers.put(streaming, this::handleModalStreamingActivity);
    modalHandlers.put(listening, this::handleModalListeningActivity);
    modalHandlers.put(watching, this::handleModalWatchingActivity);

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

  private void makeModalPlayingActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(playing, settingProvider.getText("set_activity_command.modal.playing_name"))
        .addActionRow(TextInput
            .create(playing, settingProvider.getText("set_activity_command.modal.playing_input"), TextInputStyle.SHORT)
            .setRequiredRange(0, 100).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", playing);
  }

  private void makeModalStreamingActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(streaming, settingProvider.getText("set_activity_command.modal.streaming_name"))
        .addActionRow(TextInput.create(streaming, settingProvider.getText("set_activity_command.modal.streaming_input"),
            TextInputStyle.SHORT).setRequiredRange(0, 500).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", streaming);
  }

  private void makeModalListeningActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(listening, settingProvider.getText("set_activity_command.modal.listening_name"))
        .addActionRow(TextInput.create(listening, settingProvider.getText("set_activity_command.modal.listening_input"),
            TextInputStyle.SHORT).setRequiredRange(0, 100).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", listening);
  }

  private void makeModalWatchingActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(watching, settingProvider.getText("set_activity_command.modal.watching_name"))
        .addActionRow(TextInput.create(watching, settingProvider.getText("set_activity_command.modal.watching_input"),
            TextInputStyle.SHORT).setRequiredRange(0, 100).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", watching);
  }

  private void handleModalPlayingActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", playing);
    String input = modalEvent.getValue(playing).getAsString();

    activityManager.setPlaying(input);
    modalEvent.reply(settingProvider.getText("set_activity_command.message.activity_update")).setEphemeral(true)
        .queue();
  }

  private void handleModalStreamingActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", streaming);
    String input = modalEvent.getValue(streaming).getAsString();

    activityManager.setStreaming(input);
    modalEvent.reply(settingProvider.getText("set_activity_command.message.activity_update")).setEphemeral(true)
        .queue();
  }

  private void handleModalListeningActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", listening);
    String input = modalEvent.getValue(listening).getAsString();

    activityManager.setListening(input);
    modalEvent.reply(settingProvider.getText("set_activity_command.message.activity_update")).setEphemeral(true)
        .queue();
  }

  private void handleModalWatchingActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", watching);
    String input = modalEvent.getValue(watching).getAsString();

    activityManager.setWatching(input);
    modalEvent.reply(settingProvider.getText("set_activity_command.message.activity_update")).setEphemeral(true)
        .queue();
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

  private void handleUnknownModal(ModalInteractionEvent modalEvent) {
    modalEvent.reply(settingProvider.getText("setting.message.modal.unknown")).setEphemeral(true).queue();
    log.debug("Clicked modal button");
  }
}
