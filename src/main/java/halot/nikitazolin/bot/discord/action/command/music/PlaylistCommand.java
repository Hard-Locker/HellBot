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
import halot.nikitazolin.bot.localization.action.command.music.MusicProvider;
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
  private final MusicProvider musicProvider;
  private final SettingProvider settingProvider;

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
    return musicProvider.getText("playlist.description");
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

    Button closeButton = Button.danger(close, settingProvider.getText("setting.button.close"));
    Button addPlaylistPathButton = Button.primary(addPlaylist, musicProvider.getText("playlist.button.add_playlist"));
    Button removePlaylistPathButton = Button.primary(removePlaylist,
        musicProvider.getText("playlist.button.remove_playlist"));
    List<Button> buttons = List.of(closeButton, addPlaylistPathButton, removePlaylistPathButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder();
    messageContent.append("**" + musicProvider.getText("playlist.message.title") + "**");
    messageContent.append(newLine);

    Map<String, String> playlists = settings.getPlaylists();
    if (playlists != null && !playlists.isEmpty()) {
      messageContent.append(musicProvider.getText("playlist.message.current_playlist") + ":");
      messageContent.append(newLine);

      for (Entry<String, String> entry : playlists.entrySet()) {
        messageContent.append(musicProvider.getText("playlist.message.name_playlist") + ": ");
        messageContent.append("**" + entry.getKey() + "**");
        messageContent.append(" " + musicProvider.getText("playlist.message.path_playlist") + ": ");
        messageContent.append(entry.getValue());
        messageContent.append(newLine);
      }
    }

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithActionRow(context.getTextChannel(), messageCreateData, buttons);

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

  private void makeModalAddPlaylist(ButtonInteractionEvent buttonEvent) {
    TextInput nameInput = TextInput
        .create("name", musicProvider.getText("playlist.modal.add_playlist_name"), TextInputStyle.SHORT)
        .setPlaceholder(musicProvider.getText("playlist.modal.add_playlist_name_input")).setMinLength(1)
        .setMaxLength(50).build();

    TextInput pathInput = TextInput
        .create("path", musicProvider.getText("playlist.modal.add_playlist_path"), TextInputStyle.SHORT)
        .setPlaceholder(musicProvider.getText("playlist.modal.add_playlist_path_input")).setMinLength(1)
        .setMaxLength(2000).build();

    Modal modal = Modal.create(addPlaylist, musicProvider.getText("playlist.modal.add_name"))
        .addComponents(ActionRow.of(nameInput), ActionRow.of(pathInput)).build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", addPlaylist);
  }

  private void makeModalRemovePlaylist(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(removePlaylist, musicProvider.getText("playlist.modal.remove_name"))
        .addActionRow(
            TextInput.create(removePlaylist, musicProvider.getText("playlist.modal.remove_input"), TextInputStyle.SHORT)
                .setRequiredRange(0, 50).build())
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

        modalEvent.reply(nameInput + " " + musicProvider.getText("playlist.message.add_success")).setEphemeral(true)
            .queue();
      } else {
        modalEvent.reply(musicProvider.getText("playlist.message.add_already_exists")).setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply(musicProvider.getText("playlist.message.add_error")).setEphemeral(true).queue();
    }
  }

  private void handleModalRemovePlaylist(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", removePlaylist);
    String input = modalEvent.getValue(removePlaylist).getAsString();

    if (settings.getPlaylists() != null && settings.getPlaylists().containsKey(input)) {
      settings.getPlaylists().remove(input);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      modalEvent.reply(input + " " + musicProvider.getText("playlist.message.remove_success")).setEphemeral(true)
          .queue();
    } else {
      modalEvent.reply(musicProvider.getText("playlist.message.remove_not_found")).setEphemeral(true).queue();
    }
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
