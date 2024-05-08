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
import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class VolumeCommand extends BotCommand {

  private final PlayerService playerService;
  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "volume";
  private final String close = "close";
  private final String volume = "volume";
  private Map<String, Consumer<ButtonInteractionEvent>> buttonHandlers;
  private Map<String, Consumer<ModalInteractionEvent>> modalHandlers;

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
    return "Set volume level";
  }

  @Override
  public String requiredRole() {
    return null;
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
    return new OptionData[] { new OptionData(OptionType.STRING, "volume", "Volume level (0-150)", false) };
  }

  @Override
  public void execute(BotCommandContext context) {
    List<String> args = context.getCommandArguments().getString();

    if (args.isEmpty()) {
      makeGui(context);
    } else {
      try {
        int volumeLevel = Integer.parseInt(args.get(0));
        updateVolume(volumeLevel);
      } catch (NumberFormatException e) {
        log.warn("Error parsing volume level from arguments", e);
      } catch (IndexOutOfBoundsException e) {
        log.warn("Error accessing the first argument for volume level", e);
      }
    }
  }

  private void makeGui(BotCommandContext context) {
    Button closeButton = Button.danger(close, "Close settings");
    Button volumeButton = Button.primary(volume, "Set volume");
    List<Button> buttons = List.of(closeButton, volumeButton);

    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), "Volume setting", buttons);

    buttonHandlers = new HashMap<>();
    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(volume, this::makeVolumeModal);

    modalHandlers = new HashMap<>();
    modalHandlers.put(volume, this::handleVolumeModal);

    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName));
  }

  private void updateVolume(int volumeLevel) {
    playerService.setVolume(volumeLevel);
    settings.setVolume(volumeLevel);
    settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);
    log.debug("Current volume level: {}", volumeLevel);
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    String componentId = buttonEvent.getComponentId();
    buttonHandlers.getOrDefault(componentId, this::handleUnknownButton).accept(buttonEvent);
  }

  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    String modalId = modalEvent.getModalId();
    modalHandlers.getOrDefault(modalId, this::handleUnknownModal).accept(modalEvent);
  }

  private void selectClose(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply("Settings closed").setEphemeral(true).queue();
    buttonEvent.getMessage().delete().queue();
    log.debug("Settings closed");
  }

  private void makeVolumeModal(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal
        .create(volume, "Set Volume").addActionRow(TextInput
            .create("volumeInput", "Volume Level (0-150)", TextInputStyle.SHORT).setRequiredRange(0, 150).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened volume modal");
  }

  private void handleVolumeModal(ModalInteractionEvent modalEvent) {
    String input = modalEvent.getValue("volumeInput").getAsString();

    try {
      int volumeLevel = Integer.parseInt(input);
      updateVolume(volumeLevel);
      modalEvent.reply("Current volume level: " + volumeLevel).setEphemeral(true).queue();
    } catch (NumberFormatException e) {
      log.warn("Error parsing volume level from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Error accessing the first argument for volume level", e);
    }
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
