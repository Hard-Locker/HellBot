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
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.discord.tool.StatusManager;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.command.setting.SettingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetStatusCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final StatusManager statusManager;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;
  private final SettingProvider settingProvider;

  private final String commandName = "status";
  private final String close = "close";
  private final String online = "online";
  private final String idle = "idle";
  private final String dnd = "dnd";
  private final String invisible = "invisible";

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
    return settingProvider.getText("set_status.description");
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
    Button onlineButton = Button.success(online, settingProvider.getText("set_status.button.online"));
    Button idleButton = Button.primary(idle, settingProvider.getText("set_status.button.idle"));
    Button dndButton = Button.danger(dnd, settingProvider.getText("set_status.button.dnd"));
    Button invisibleButton = Button.secondary(invisible, settingProvider.getText("set_status.button.invisible"));
    List<Button> buttons = List.of(closeButton, onlineButton, idleButton, dndButton, invisibleButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder();
    messageContent.append("**" + settingProvider.getText("set_status.message.title") + "**");
    messageContent.append(newLine);

    messageContent.append(settingProvider.getText("set_status.message.subtitle") + "?");
    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithActionRow(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(online, this::setStatus);
    buttonHandlers.put(idle, this::setStatus);
    buttonHandlers.put(dnd, this::setStatus);
    buttonHandlers.put(invisible, this::setStatus);

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

  @Override
  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    return;
  }

  @Override
  public void stringSelectProcessing(StringSelectInteractionEvent stringSelectEvent) {
    return;
  }

  private void setStatus(ButtonInteractionEvent buttonEvent) {
    switch (buttonEvent.getComponentId()) {
    case online:
      statusManager.setOnline();
      buttonEvent.reply(settingProvider.getText("set_status.message.set_online")).setEphemeral(true).queue();
      break;

    case idle:
      statusManager.setIdle();
      buttonEvent.reply(settingProvider.getText("set_status.message.set_idle")).setEphemeral(true).queue();
      break;

    case dnd:
      statusManager.setDnd();
      buttonEvent.reply(settingProvider.getText("set_status.message.set_dnd")).setEphemeral(true).queue();
      break;

    case invisible:
      statusManager.setInvisible();
      buttonEvent.reply(settingProvider.getText("set_status.message.set_invisible")).setEphemeral(true).queue();
      break;

    default:
      buttonEvent.reply(settingProvider.getText("set_status.message.unknown")).setEphemeral(true).queue();
      break;
    }

    log.debug("Status changed to " + buttonEvent.getComponentId());
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
