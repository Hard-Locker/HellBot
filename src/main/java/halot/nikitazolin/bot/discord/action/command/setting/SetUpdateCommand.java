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
import halot.nikitazolin.bot.util.VersionChecker;
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
public class SetUpdateCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;
  private final VersionChecker versionChecker;
  private final SettingProvider settingProvider;

  private final String commandName = "update";
  private final String close = "close";
  private final String enableUpdateNotifier = "enableUpdateNotifier";
  private final String disableUpdateNotifier = "disableUpdateNotifier";

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
    return settingProvider.getText("set_update_command.description");
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

    String latestVersion = versionChecker.getNumberLatestVersion()
        .orElse(settingProvider.getText("setting.text.unknown"));
    String currentVersion = versionChecker.getNumberCurrentVersion()
        .orElse(settingProvider.getText("setting.text.unknown"));

    Button closeButton = Button.danger(close, settingProvider.getText("setting.button.close"));
    Button enableButton = Button.primary(enableUpdateNotifier,
        settingProvider.getText("set_update_command.button.enable"));
    Button disableButton = Button.primary(disableUpdateNotifier,
        settingProvider.getText("set_update_command.button.disable"));
    List<Button> buttons = List.of(closeButton, enableButton, disableButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder();
    messageContent.append("**" + settingProvider.getText("set_update_command.message.title") + "**");
    messageContent.append(newLine);

    messageContent.append(
        settingProvider.getText("set_update_command.message.current_version") + ": " + "**" + currentVersion + "**");
    messageContent.append(newLine);
    messageContent.append(
        settingProvider.getText("set_update_command.message.latest_version") + ": " + "**" + latestVersion + "**");
    messageContent.append(newLine);
    messageContent.append(settingProvider.getText("set_update_command.message.notify") + ": ");
    messageContent
        .append(settings.isUpdateNotification() == true ? "**" + settingProvider.getText("setting.text.yes") + "**"
            : "**" + settingProvider.getText("setting.text.no") + "**");
    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(enableUpdateNotifier, this::handleButtonEnable);
    buttonHandlers.put(disableUpdateNotifier, this::handleButtonDisable);

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

  private void handleButtonEnable(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", enableUpdateNotifier);
    settings.setUpdateNotification(true);
    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

    String info = settings.isUpdateNotification() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no");
    buttonEvent.reply(settingProvider.getText("set_update_command.message.set") + ": " + info).setEphemeral(true)
        .queue();
  }

  private void handleButtonDisable(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", disableUpdateNotifier);
    settings.setUpdateNotification(false);
    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

    String info = settings.isUpdateNotification() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no");
    buttonEvent.reply(settingProvider.getText("set_update_command.message.set") + ": " + info).setEphemeral(true)
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
}
