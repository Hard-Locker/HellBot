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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetAloneCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;
  private final SettingProvider settingProvider;

  private final String commandName = "alone";
  private final String close = "close";
  private final String aloneTime = "aloneTime";
  private final String stayInChannel = "stayInChannel";

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
    return settingProvider.getText("set_alone.description");
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
    Button aloneTimeButton = Button.primary(aloneTime, settingProvider.getText("set_alone.button.set_alone_time"));
    Button stayInChannelButton = Button.primary(stayInChannel, settingProvider.getText("set_alone.button.set_stay"));
    ActionRow rowButtons = ActionRow.of(closeButton, aloneTimeButton, stayInChannelButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder();
    messageContent.append("**" + settingProvider.getText("set_alone.message.title") + "**");
    messageContent.append(newLine);

    messageContent.append(settingProvider.getText("set_alone.message.alone_time") + ": ");
    messageContent.append(settings.getAloneTimeUntilStop() == 0 ? settingProvider.getText("setting.text.not_set")
        : settings.getAloneTimeUntilStop());
    messageContent.append(newLine);

    messageContent.append(settingProvider.getText("set_alone.message.stay_in_channel") + ": ");
    messageContent.append(settings.isStayInChannel() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no"));
    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithActionRow(context.getTextChannel(), messageCreateData, List.of(rowButtons));

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(aloneTime, this::makeModalAloneTime);
    buttonHandlers.put(stayInChannel, this::handleButtonStayInChannel);

    modalHandlers.put(aloneTime, this::handleModalAloneTime);

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
    if (checkUserAccess(modalEvent.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(modalEvent.getUser());
      log.debug("User {} does not have access to use: {}", modalEvent.getUser(), commandName);

      return;
    }

    String modalId = modalEvent.getModalId();
    modalHandlers.getOrDefault(modalId, this::handleUnknownModal).accept(modalEvent);
  }

  @Override
  public void stringSelectProcessing(StringSelectInteractionEvent stringSelectEvent) {
    return;
  }

  private void makeModalAloneTime(ButtonInteractionEvent buttonEvent) {
    TextInput input = TextInput
        .create(aloneTime, TextInputStyle.SHORT)
        .setPlaceholder(settingProvider.getText("set_alone.modal.alone_input_description"))
        .setMinLength(1)
        .setMaxLength(5)
        .build();

    Modal modal = Modal.create(aloneTime, settingProvider.getText("set_alone.modal.alone_name"))
        .addComponents(Label.of(settingProvider.getText("set_alone.modal.alone_input"), input))
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", aloneTime);
  }

  private void handleModalAloneTime(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", aloneTime);
    String input = modalEvent.getValue(aloneTime).getAsString();

    try {
      int time = Integer.parseInt(input);
      settings.setAloneTimeUntilStop(time);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      modalEvent.reply(settingProvider.getText("set_alone.message.alone_time_result") + ": " + time).setEphemeral(true)
          .queue();
      log.debug("User changed alone time to {} seconds", time);
    } catch (NumberFormatException e) {
      log.debug("Error parsing user ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for user ID", e);
    }
  }

  //TODO The "stay in channel" change is not displayed correctly.
  private void handleButtonStayInChannel(ButtonInteractionEvent buttonEvent) {
    log.debug("Processing setting: {}", stayInChannel);

    if (settings.isStayInChannel() == true) {
      settings.setStayInChannel(false);
    } else {
      settings.setStayInChannel(true);
    }

    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
    String info = settings.isStayInChannel() == true ? settingProvider.getText("setting.text.yes")
        : settingProvider.getText("setting.text.no");
    buttonEvent.reply(settingProvider.getText("set_alone.stay_result") + ": " + info).setEphemeral(true).queue();
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
