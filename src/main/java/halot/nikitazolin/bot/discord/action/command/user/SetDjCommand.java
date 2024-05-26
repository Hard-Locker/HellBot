package halot.nikitazolin.bot.discord.action.command.user;

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
import halot.nikitazolin.bot.discord.tool.DiscordDataReceiver;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
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
public class SetDjCommand extends BotCommand {

  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final DiscordDataReceiver discordDataReceiver;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "dj";
  private final String close = "close";
  private final String addDj = "addDjUser";
  private final String removeDj = "removeDjUser";

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
    return "Set DJ";
  }

  @Override
  public boolean checkUserPermission(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getOwnerUserId() != null) {
      allowedIds.add(settings.getOwnerUserId());
    }

    if (settings.getAdminUserIds() != null) {
      allowedIds.addAll(settings.getAdminUserIds());
    }

    if (allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
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
    if (checkUserPermission(context.getUser()) == false) {
      EmbedBuilder embed = messageFormatter.createAltInfoEmbed("You have not permission for use this command");
      messageSender.sendPrivateMessage(context.getUser(), embed);
      log.debug("User have not permission for use settings" + context.getUser());

      return;
    }

    Button closeButton = Button.danger(close, "Close settings");
    Button addDjButton = Button.primary(addDj, "Add DJ");
    Button removeDjButton = Button.primary(removeDj, "Remove DJ");
    List<Button> buttons = List.of(closeButton, addDjButton, removeDjButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder("**DJ setting**").append(newLine);

    if (settings.getDjUserIds() != null && !settings.getDjUserIds().isEmpty()) {
      messageContent.append("Current DJ:").append(newLine);
      List<User> users = discordDataReceiver.getUsersByIds(settings.getDjUserIds());

      for (User user : users) {
        messageContent.append(user.getAsMention());
        messageContent.append(" ID: ");
        messageContent.append(user.getIdLong());
        messageContent.append(newLine);
      }
    }

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(addDj, this::makeModalAddDj);
    buttonHandlers.put(removeDj, this::makeModalRemoveDj);

    modalHandlers.put(addDj, this::handleModalAddDj);
    modalHandlers.put(removeDj, this::handleModalRemoveDj);

    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName, 300000));
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    if (checkUserPermission(buttonEvent.getUser()) == false) {
      EmbedBuilder embed = messageFormatter.createAltInfoEmbed("You have not permission for use this command");
      messageSender.sendPrivateMessage(buttonEvent.getUser(), embed);
      log.debug("User have not permission for use settings" + buttonEvent.getUser());

      return;
    }

    String componentId = buttonEvent.getComponentId();
    buttonHandlers.getOrDefault(componentId, this::handleUnknownButton).accept(buttonEvent);
  }

  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    if (checkUserPermission(modalEvent.getUser()) == false) {
      EmbedBuilder embed = messageFormatter.createAltInfoEmbed("You have not permission for use this command");
      messageSender.sendPrivateMessage(modalEvent.getUser(), embed);
      log.debug("User have not permission for use settings" + modalEvent.getUser());

      return;
    }

    String modalId = modalEvent.getModalId();
    modalHandlers.getOrDefault(modalId, this::handleUnknownModal).accept(modalEvent);
  }

  private void makeModalAddDj(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(addDj, "Add DJ")
        .addActionRow(
            TextInput.create(addDj, "Enter user ID to add DJ", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", addDj);
  }

  private void makeModalRemoveDj(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal
        .create(removeDj, "Remove DJ").addActionRow(TextInput
            .create(removeDj, "Enter user ID to remove DJ", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", removeDj);
  }

  private void handleModalAddDj(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", addDj);
    String input = modalEvent.getValue(addDj).getAsString();
    Long userId = null;

    try {
      userId = Long.parseLong(input);
    } catch (NumberFormatException e) {
      log.debug("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for user ID", e);
    }

    User user = discordDataReceiver.getUserById(userId);

    if (user != null && settings.getDjUserIds() != null) {
      settings.getDjUserIds().add(userId);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      modalEvent.reply(user.getAsMention() + " has been added").setEphemeral(true).queue();
    } else {
      modalEvent.reply("User not found").setEphemeral(true).queue();
    }
  }

  private void handleModalRemoveDj(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", removeDj);
    String input = modalEvent.getValue(removeDj).getAsString();
    Long userId = null;

    try {
      userId = Long.parseLong(input);
    } catch (NumberFormatException e) {
      log.debug("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for user ID", e);
    }

    if (settings.getDjUserIds() != null && settings.getDjUserIds().contains(userId)) {
      settings.getDjUserIds().remove(userId);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      User user = discordDataReceiver.getUserById(userId);

      if (user != null) {
        modalEvent.reply(user.getAsMention() + " has been removed from this list").setEphemeral(true).queue();
      } else {
        modalEvent.reply(userId + " has been removed from this list").setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply("User not found in this list").setEphemeral(true).queue();
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
