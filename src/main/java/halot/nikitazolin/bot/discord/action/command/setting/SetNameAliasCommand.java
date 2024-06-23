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
import halot.nikitazolin.bot.discord.action.CommandCollector;
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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SetNameAliasCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;
  private final CommandCollector commandCollector;
  private final SettingProvider settingProvider;

  private final String commandName = "name";
  private final String close = "close";
  private final String next = "nextPage";
  private final String previous = "previousPage";
  private final String addName = "addNameAlias";
  private final String removeName = "removeNameAlias";
  private final String menuName = "menuNameAlias";

  private Map<String, Consumer<ButtonInteractionEvent>> buttonHandlers = new HashMap<>();
  private Map<String, Consumer<ModalInteractionEvent>> modalHandlers = new HashMap<>();
  private Map<String, Consumer<StringSelectInteractionEvent>> stringSelectHandlers = new HashMap<>();

  private int currentPage = 0;
  private static final int PAGE_SIZE = 25;
  private String selectedName = "";

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
    return settingProvider.getText("set_name_alias_command.description");
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

    currentPage = 0;
    List<ActionRow> actionRows = createSelectMenu(currentPage);

    Long messageId = messageSender.sendMessageWithActionRow(context.getTextChannel(),
        settingProvider.getText("set_name_alias_command.message.title") + ":", actionRows);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(next, this::nextPage);
    buttonHandlers.put(previous, this::previousPage);
    buttonHandlers.put(addName, this::makeModalAddName);
    buttonHandlers.put(removeName, this::makeModalRemoveName);

    stringSelectHandlers.put(menuName, this::makeMessageManageNameAlias);

    modalHandlers.put(addName, this::handleModalAddName);
    modalHandlers.put(removeName, this::handleModalRemoveName);

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
    if (checkUserAccess(stringSelectEvent.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(stringSelectEvent.getUser());
      log.debug("User {} does not have access to use: {}", stringSelectEvent.getUser(), commandName);

      return;
    }

    String menuId = stringSelectEvent.getComponentId();
    stringSelectHandlers.getOrDefault(menuId, this::handleUnknownStringSelect).accept(stringSelectEvent);
  }

  private List<ActionRow> createSelectMenu(int pageIndex) {
    List<ActionRow> actionRows = new ArrayList<>();
    List<String> commandNames = commandCollector.getActiveCommands().stream().map(BotCommand::name).toList();

    int fromIndex = pageIndex * PAGE_SIZE;
    int toIndex = Math.min(fromIndex + PAGE_SIZE, commandNames.size());
    List<String> pageCommands = commandNames.subList(fromIndex, toIndex);

    StringSelectMenu.Builder selectMenuBuilder = StringSelectMenu.create(menuName)
        .setPlaceholder(settingProvider.getText("set_name_alias_command.message.subtitle"));

    List<SelectOption> selectOptions = pageCommands.stream().map(option -> SelectOption.of(option, option)).toList();
    selectMenuBuilder.addOptions(selectOptions);

    StringSelectMenu selectMenu = selectMenuBuilder.build();

    Button closeButton = Button.danger(close, settingProvider.getText("setting.button.close"));
    Button nextButton = Button.primary(next, settingProvider.getText("setting.button.next"))
        .withDisabled(toIndex == commandNames.size());
    Button previousButton = Button.primary(previous, settingProvider.getText("setting.button.previous"))
        .withDisabled(fromIndex == 0);
    List<Button> buttons = List.of(previousButton, closeButton, nextButton);

    actionRows.addAll(List.of(ActionRow.of(selectMenu), ActionRow.of(buttons)));

    return actionRows;
  }

  private void nextPage(ButtonInteractionEvent event) {
    currentPage++;
    List<ActionRow> actionRows = createSelectMenu(currentPage);
    event.editComponents(actionRows).queue();
  }

  private void previousPage(ButtonInteractionEvent event) {
    currentPage--;
    List<ActionRow> actionRows = createSelectMenu(currentPage);
    event.editComponents(actionRows).queue();
  }

  private void makeMessageManageNameAlias(StringSelectInteractionEvent stringSelectEvent) {
    String selectedOption = stringSelectEvent.getValues().get(0);
    selectedName = selectedOption;

    if (settings.getNameAliases() == null) {
      stringSelectEvent.reply(settingProvider.getText("setting.message.internal_error") + ": " + selectedOption)
          .setEphemeral(true).queue();
      return;
    } else {
      stringSelectEvent
          .reply(settingProvider.getText("set_name_alias_command.message.open_setting") + ": " + selectedOption)
          .setEphemeral(true).queue();
    }

    List<String> nameAliases = settings.getNameAliases().containsKey(selectedOption)
        ? settings.getNameAliases().get(selectedOption)
        : List.of(settingProvider.getText("setting.text.not_set"));

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder();
    messageContent.append("**" + settingProvider.getText("set_name_alias_command.message.current_name_alias") + ": "
        + "**" + selectedOption);
    messageContent.append(newLine);

    for (String nameAlias : nameAliases) {
      messageContent.append(nameAlias);
      messageContent.append(newLine);
    }

    Button closeButton = Button.danger(close, settingProvider.getText("setting.button.close"));
    Button addNameButton = Button.primary(addName, settingProvider.getText("set_name_alias_command.button.add_name"));
    Button removeNameButton = Button.primary(removeName,
        settingProvider.getText("set_name_alias_command.button.remove_name"));
    List<Button> buttons = List.of(closeButton, addNameButton, removeNameButton);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithActionRow(stringSelectEvent.getChannel().asTextChannel(),
        messageCreateData, buttons);
    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName, 300000));
  }

  private void makeModalAddName(ButtonInteractionEvent buttonEvent) {
    TextInput input = TextInput
        .create(addName, settingProvider.getText("set_name_alias_command.modal.add_name_input"), TextInputStyle.SHORT)
        .setPlaceholder(settingProvider.getText("set_name_alias_command.modal.add_name_input_description"))
        .setMinLength(1).setMaxLength(20).build();

    Modal modal = Modal
        .create(addName, settingProvider.getText("set_name_alias_command.modal.add_name") + ": " + selectedName)
        .addComponents(ActionRow.of(input)).build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", addName);
  }

  private void makeModalRemoveName(ButtonInteractionEvent buttonEvent) {
    TextInput input = TextInput
        .create(removeName, settingProvider.getText("set_name_alias_command.modal.remove_name_input"),
            TextInputStyle.SHORT)
        .setPlaceholder(settingProvider.getText("set_name_alias_command.modal.remove_name_input_description"))
        .setMinLength(1).setMaxLength(20).build();

    Modal modal = Modal
        .create(removeName, settingProvider.getText("set_name_alias_command.modal.remove_name") + ": " + selectedName)
        .addComponents(ActionRow.of(input)).build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", removeName);
  }

  private void handleModalAddName(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", addName);
    String input = modalEvent.getValue(addName).getAsString();

    if (input == null && settings.getNameAliases() == null) {
      modalEvent.reply(settingProvider.getText("setting.message.internal_error")).setEphemeral(true).queue();
      return;
    }

    modalEvent.deferReply(true).queue(hook -> {
      synchronized (settings) {
        if (settings.getNameAliases().containsKey(selectedName)) {
          if (settings.getNameAliases().get(selectedName).contains(input)) {
            hook.editOriginal(
                input + " " + settingProvider.getText("set_name_alias_command.message.add_name_already_exists"))
                .queue();
          } else {
            settings.getNameAliases().get(selectedName).add(input);
            settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

            hook.editOriginal(input + " " + settingProvider.getText("set_name_alias_command.message.add_name_success"))
                .queue();

            log.debug("Added name alias: {}, for command: {}", input, selectedName);
          }
        } else {
          settings.getNameAliases().put(selectedName, new ArrayList<>(List.of(input)));
          settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

          hook.editOriginal(input + " " + settingProvider.getText("set_name_alias_command.message.add_name_success"))
              .queue();

          log.debug("Added name alias: {}, for command: {}", input, selectedName);
        }
      }
    });
  }

  private void handleModalRemoveName(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", removeName);
    String input = modalEvent.getValue(removeName).getAsString();

    if (input == null && settings.getNameAliases() == null) {
      modalEvent.reply(settingProvider.getText("setting.message.internal_error")).setEphemeral(true).queue();
      return;
    }

    modalEvent.deferReply(true).queue(hook -> {
      synchronized (settings) {
        if (settings.getNameAliases().containsKey(selectedName)) {
          if (settings.getNameAliases().get(selectedName).contains(input)) {
            settings.getNameAliases().get(selectedName).remove(input);

            if (settings.getNameAliases().get(selectedName).isEmpty()) {
              settings.getNameAliases().remove(selectedName);
            }

            settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
            hook.editOriginal(
                input + " " + settingProvider.getText("set_name_alias_command.message.remove_name_success")).queue();

            log.debug("Removed name alias: {}, for command: {}", input, selectedName);
          } else {
            hook.editOriginal(
                input + " " + settingProvider.getText("set_name_alias_command.message.remove_name_not_found")).queue();
          }
        } else {
          hook.editOriginal(
              input + " " + settingProvider.getText("set_name_alias_command.message.remove_name_not_found")).queue();
        }
      }
    });
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

  private void handleUnknownStringSelect(StringSelectInteractionEvent stringSelectEvent) {
    stringSelectEvent.reply(settingProvider.getText("setting.message.string_select.unknown")).setEphemeral(true)
        .queue();
    log.debug("Clicked string select");
  }
}
