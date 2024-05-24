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
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
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
public class SetUserCommand extends BotCommand {

  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "user";
  private final String close = "close";
  private final String addAdmin = "addAdminUser";
  private final String removeAdmin = "removeAdminUser";
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
    return "Change user settings";
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
    Button addAdminButton = Button.primary(addAdmin, "Add admin");
    Button removeAdminButton = Button.primary(removeAdmin, "Remove admin");
    Button addDjButton = Button.primary(addDj, "Add DJ");
    Button removeDjButton = Button.primary(removeDj, "Remove DJ");
    List<Button> buttons = List.of(closeButton, addAdminButton, removeAdminButton, addDjButton, removeDjButton);

    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), "Which role need update?", buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(addAdmin, this::makeAddAdmin);
    buttonHandlers.put(removeAdmin, this::makeRemoveAdmin);
    buttonHandlers.put(addDj, this::makeAddDj);
    buttonHandlers.put(removeDj, this::makeRemoveDj);

    modalHandlers.put(addAdmin, this::handleModalAddAdmin);
    modalHandlers.put(removeAdmin, this::handleModalRemoveAdmin);
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

  private void makeAddAdmin(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal
        .create(addAdmin, "Add admin").addActionRow(TextInput
            .create(addAdmin, "Enter user ID to add admin", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened admin adding modal");
  }

  private void makeRemoveAdmin(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal
        .create(removeAdmin, "Remove admin").addActionRow(TextInput
            .create(removeAdmin, "Enter user ID to remove admin", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened admin remove modal");
  }

  private void handleModalAddAdmin(ModalInteractionEvent modalEvent) {
    String adminAdd = modalEvent.getValue(addAdmin).getAsString();
    List<Member> members = modalEvent.getGuild().getMembers();
    List<Long> memberIds = members.stream().map(Member::getIdLong).toList();

    try {
      long userId = Long.parseLong(adminAdd);

      if (memberIds.contains(userId)) {
        settings.getAdminUserIds().add(userId);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
        User user = modalEvent.getJDA().getUserById(userId);
        modalEvent.reply(user.getAsMention() + " has been added as admin.").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Failed to find user.").setEphemeral(true).queue();
      }
    } catch (NumberFormatException e) {
      log.warn("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Error accessing the first argument for user ID", e);
    }
  }

  private void handleModalRemoveAdmin(ModalInteractionEvent modalEvent) {
    String adminRemove = modalEvent.getValue(removeAdmin).getAsString();
    List<Long> adminIds = settings.getAdminUserIds();

    try {
      long userId = Long.parseLong(adminRemove);

      if (adminIds.contains(userId)) {
        settings.getAdminUserIds().remove(userId);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
        User user = modalEvent.getJDA().getUserById(userId);
        modalEvent.reply(user.getAsMention() + " has been remove as admin.").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Failed to find user.").setEphemeral(true).queue();
      }
    } catch (NumberFormatException e) {
      log.warn("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Error accessing the first argument for user ID", e);
    }
  }

  private void makeAddDj(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(addDj, "Add DJ")
        .addActionRow(
            TextInput.create(addDj, "Enter user ID to add DJ", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened DJ adding modal");
  }

  private void makeRemoveDj(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal
        .create(removeDj, "Remove DJ").addActionRow(TextInput
            .create(removeDj, "Enter user ID to remove DJ", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened DJ remove modal");
  }

  private void handleModalAddDj(ModalInteractionEvent modalEvent) {
    String adminAdd = modalEvent.getValue(addDj).getAsString();
    List<Member> members = modalEvent.getGuild().getMembers();
    List<Long> memberIds = members.stream().map(Member::getIdLong).toList();

    try {
      long userId = Long.parseLong(adminAdd);

      if (memberIds.contains(userId)) {
        settings.getDjUserIds().add(userId);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
        User user = modalEvent.getJDA().getUserById(userId);
        modalEvent.reply(user.getAsMention() + " has been added as DJ").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Failed to find user.").setEphemeral(true).queue();
      }
    } catch (NumberFormatException e) {
      log.warn("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Error accessing the first argument for user ID", e);
    }
  }

  private void handleModalRemoveDj(ModalInteractionEvent modalEvent) {
    String adminRemove = modalEvent.getValue(removeDj).getAsString();
    List<Long> adminIds = settings.getAdminUserIds();

    try {
      long userId = Long.parseLong(adminRemove);

      if (adminIds.contains(userId)) {
        settings.getDjUserIds().remove(userId);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
        User user = modalEvent.getJDA().getUserById(userId);
        modalEvent.reply(user.getAsMention() + " has been remove as DJ").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Failed to find user.").setEphemeral(true).queue();
      }
    } catch (NumberFormatException e) {
      log.warn("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Error accessing the first argument for user ID", e);
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
