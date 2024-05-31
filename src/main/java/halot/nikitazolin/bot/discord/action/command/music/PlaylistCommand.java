package halot.nikitazolin.bot.discord.action.command.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import net.dv8tion.jda.api.interactions.components.ActionRow;
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
public class PlaylistCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "playlist";
  private final String close = "close";
  private final String addPlaylist = "addPlaylist";
  private final String removePlaylist = "removePlaylist";

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
    return "Set playlists";
  }

  @Override
  public boolean checkUserAccess(User user) {
    return allowChecker.isOwnerOrAdminOrDj(user);
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
    Button addPlaylistPathButton = Button.primary(addPlaylist, "Add playlist");
    Button removePlaylistPathButton = Button.primary(removePlaylist, "Remove playlist");
    List<Button> buttons = List.of(closeButton, addPlaylistPathButton, removePlaylistPathButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder("**Settings playlists**").append(newLine);

    Map<String, String> playlists = settings.getPlaylists();
    if (playlists != null && !playlists.isEmpty()) {
      messageContent.append("Current playlists:").append(newLine);

      for (Entry<String, String> entry : playlists.entrySet()) {
        messageContent.append("Name: **");
        messageContent.append(entry.getKey());
        messageContent.append("** Path: ");
        messageContent.append(entry.getValue());
        messageContent.append(newLine);
      }
    }

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(addPlaylist, this::makeModalAddPlaylist);
    buttonHandlers.put(removePlaylist, this::makeModalRemovePlaylist);

    modalHandlers.put(addPlaylist, this::handleModalAddPlaylist);
    modalHandlers.put(removePlaylist, this::handleModalRemovePlaylist);

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

  private void makeModalAddPlaylist(ButtonInteractionEvent buttonEvent) {
    TextInput nameInput = TextInput.create("name", "Playlist name", TextInputStyle.SHORT)
        .setPlaceholder("Enter future playlist name").setMinLength(1).setMaxLength(50).build();

    TextInput pathInput = TextInput.create("path", "Playlist path", TextInputStyle.SHORT)
        .setPlaceholder("Enter path to file or folder").setMinLength(1).setMaxLength(2000).build();

    Modal modal = Modal.create(addPlaylist, "Add playlist")
        .addComponents(ActionRow.of(nameInput), ActionRow.of(pathInput)).build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", addPlaylist);
  }

  private void makeModalRemovePlaylist(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(removePlaylist, "Remove playlist").addActionRow(TextInput
        .create(removePlaylist, "Enter playlist name to delete", TextInputStyle.SHORT).setRequiredRange(0, 50).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", removePlaylist);
  }

  private void handleModalAddPlaylist(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", addPlaylist);
    String nameInput = modalEvent.getValue("name").getAsString();
    String pathInput = modalEvent.getValue("path").getAsString();

    if (settings.getPlaylists() != null) {
      if (!settings.getPlaylists().containsKey(nameInput)) {
        settings.getPlaylists().put(nameInput, pathInput);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

        modalEvent.reply(nameInput + " has been added to list").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Playlist with the same name already exists").setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply("Internal error").setEphemeral(true).queue();
    }
  }

  private void handleModalRemovePlaylist(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", removePlaylist);
    String input = modalEvent.getValue(removePlaylist).getAsString();

    if (settings.getPlaylists() != null && settings.getPlaylists().containsKey(input)) {
      settings.getPlaylists().remove(input);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      modalEvent.reply(input + " has been removed from this list").setEphemeral(true).queue();
    } else {
      modalEvent.reply("Playlist not found in this list").setEphemeral(true).queue();
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
