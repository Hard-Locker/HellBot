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
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
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
  private final String adminUserIds = "adminUserIds";
  private final String djUserIds = "djUserIds";
  private final String bannedUserIds = "bannedUserIds";

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
    Button adminUserIdsButton = Button.primary(adminUserIds, "Set admin");
    Button djUserIdsButton = Button.primary(djUserIds, "Set DJ");
    Button bannedUserIdsButton = Button.primary(bannedUserIds, "Ban user");
    List<Button> buttons = List.of(closeButton, adminUserIdsButton, djUserIdsButton, bannedUserIdsButton);

    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), "Which role need update?", buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(adminUserIds, this::makeModalAdminUser);
    buttonHandlers.put(djUserIds, this::makeModalDjUser);
    buttonHandlers.put(bannedUserIds, this::makeModalBannedUser);

    modalHandlers.put(adminUserIds, this::handleModalAdminUser);
    modalHandlers.put(djUserIds, this::handleModalDjUser);
    modalHandlers.put(bannedUserIds, this::handleModalBanUser);

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

  private void makeModalAdminUser(ButtonInteractionEvent buttonEvent) {
    List<Member> members = buttonEvent.getGuild().getMembers();
    StringSelectMenu.Builder menuBuilder = StringSelectMenu.create(adminUserIds)
        .setPlaceholder("Choose users to set as admin").setRequiredRange(1, members.size());

    for (Member member : members) {
      menuBuilder.addOption(member.getEffectiveName(), member.getId());
    }
    
    StringSelectMenu menu = menuBuilder.build();

    buttonEvent.reply("Please pick your class below").setEphemeral(true).addActionRow(menu).queue();

//    Modal modal = Modal.create(adminUserIds, "Set admin users").addActionRow(menu).build();
//    buttonEvent.replyModal(modal).queue();
    log.debug("Opened admin changer modal with user selection");
  }

  private void handleModalAdminUser(ModalInteractionEvent modalEvent) {
    String selectedUserId = modalEvent.getValues().get(0).getAsString();
    User selectedUser = modalEvent.getJDA().getUserById(selectedUserId);

    if (selectedUser != null) {
      settings.getAdminUserIds().add(selectedUser.getIdLong());
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
      modalEvent.reply("User " + selectedUser.getName() + " has been added as admin.").setEphemeral(true).queue();
    } else {
      modalEvent.reply("Failed to find the selected user.").setEphemeral(true).queue();
    }
  }

  private void makeModalDjUser(ButtonInteractionEvent buttonEvent) {
    // TODO
  }

  private void handleModalDjUser(ModalInteractionEvent modalEvent) {
    // TODO
  }

  private void makeModalBannedUser(ButtonInteractionEvent buttonEvent) {
    // TODO
  }

  private void handleModalBanUser(ModalInteractionEvent modalEvent) {
    // TODO
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
