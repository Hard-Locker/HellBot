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
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetPrefixCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "prefix";
  private final String close = "close";
  private final String addPrefix = "addPrefix";
  private final String removePrefix = "removePrefix";

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
    return "Set prefixes for received command";
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
    Button addPrefixButton = Button.primary(addPrefix, "Add prefix");
    Button removePrefixButton = Button.primary(removePrefix, "Remove prefix");
    List<Button> buttons = List.of(closeButton, addPrefixButton, removePrefixButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder("**Settings prefixes**").append(newLine);

    if (settings.getPrefixes() != null && !settings.getPrefixes().isEmpty()) {
      messageContent.append("Current prefixes:").append(newLine);

      for (String prefix : settings.getPrefixes()) {
        messageContent.append(prefix);
        messageContent.append(newLine);
      }
    }

    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(addPrefix, this::makeModalAddPrefix);
    buttonHandlers.put(removePrefix, this::makeModalRemovePrefix);

    modalHandlers.put(addPrefix, this::handleModalAddPrefix);
    modalHandlers.put(removePrefix, this::handleModalRemovePrefix);

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

  private void makeModalAddPrefix(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(addPrefix, "Add prefix")
        .addActionRow(
            TextInput.create(addPrefix, "Enter any character", TextInputStyle.SHORT).setRequiredRange(0, 5).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", addPrefix);
  }

  private void makeModalRemovePrefix(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(removePrefix, "Remove prefix")
        .addActionRow(TextInput.create(removePrefix, "Enter prefix from list in the message", TextInputStyle.SHORT)
            .setRequiredRange(0, 5).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", removePrefix);
  }

  private void handleModalAddPrefix(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", addPrefix);
    String input = modalEvent.getValue(addPrefix).getAsString();

    if (settings.getPrefixes() != null) {
      if (!settings.getPrefixes().contains(input)) {
        settings.getPrefixes().add(input);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

        modalEvent.reply("**" + input + "**" + " has been added").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Prefix has already been added to the list").setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply("Prefix not found").setEphemeral(true).queue();
    }
  }

  private void handleModalRemovePrefix(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", removePrefix);
    String input = modalEvent.getValue(removePrefix).getAsString();

    if (settings.getPrefixes() != null && settings.getPrefixes().contains(input)) {
      settings.getPrefixes().remove(input);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      modalEvent.reply("**" + input + "**" + " has been removed from this list").setEphemeral(true).queue();
    } else {
      modalEvent.reply("**" + input + "**" + " not found in this list").setEphemeral(true).queue();
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
