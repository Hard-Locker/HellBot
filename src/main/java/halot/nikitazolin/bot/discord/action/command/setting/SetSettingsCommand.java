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
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
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
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "set";
  private final String close = "close";
  private final String volume = "volume";
  private final String aloneTime = "aloneTime";
  private Map<String, Consumer<ButtonInteractionEvent>> buttonHandlers;
  private Map<String, Consumer<ModalInteractionEvent>> modalHandlers;

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
    Button closeButton = Button.danger(close, "Close settings");
    Button volumeButton = Button.primary(volume, "Set volume");
    Button aloneTimeButton = Button.primary(aloneTime, "Set alone time");
    List<Button> buttons = List.of(closeButton, volumeButton, aloneTimeButton);

    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), "Which setting need update?", buttons);

    buttonHandlers = new HashMap<>();
    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(volume, this::makeVolume);
    buttonHandlers.put(aloneTime, this::makeAloneTimeUntilStop);

    modalHandlers = new HashMap<>();
    modalHandlers.put(volume, this::setVolume);
    modalHandlers.put(aloneTime, this::setAloneTimeUntilStop);

    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName));
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    String componentId = buttonEvent.getComponentId();
    buttonHandlers.getOrDefault(componentId, this::handleUnknownButton).accept(buttonEvent);
  }

  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    String modalId = modalEvent.getModalId();
    modalHandlers.getOrDefault(modalId, this::handleUnknownModal).accept(modalEvent);
  }

  private void selectClose(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply("Settings closed").setEphemeral(true).queue();
    buttonEvent.getMessage().delete().queue();
    log.debug("Settings closed");
  }

  private void makeVolume(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(volume, "Set Volume")
        .addActionRow(TextInput.create("volumeInput", "Volume Level (0-150)", TextInputStyle.SHORT).setRequiredRange(0, 150).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened volume modal");
  }

  private void setVolume(ModalInteractionEvent modalEvent) {
    String inputVolume = modalEvent.getValue("volumeInput").getAsString();
    int volume = Integer.parseInt(inputVolume);

    settings.setVolume(volume);
    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

    modalEvent.reply("Current volume level: " + volume).setEphemeral(true).queue();
    log.debug("Current volume level: {}", volume);
  }

  private void makeAloneTimeUntilStop(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(aloneTime, "Set alone time in seconds until stop bot")
        .addActionRow(TextInput.create("aloneTimeInput", "Time (seconds 0-4000)", TextInputStyle.SHORT).setRequiredRange(0, 4000).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened alone time modal");
  }

  private void setAloneTimeUntilStop(ModalInteractionEvent modalEvent) {
    String inputTime = modalEvent.getValue("aloneTimeInput").getAsString();
    Long time = Long.parseLong(inputTime);

    settings.setAloneTimeUntilStop(time);
    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

    modalEvent.reply("Alone time set to " + time).setEphemeral(true).queue();
    log.debug("User changed alone time to " + time);
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
