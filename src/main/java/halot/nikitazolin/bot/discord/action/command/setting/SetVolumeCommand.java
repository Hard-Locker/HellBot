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
import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
public class SetVolumeCommand extends BotCommand {

  private final PlayerService playerService;
  private final MessageSender messageSender;
  private final Settings settings;
  private final AllowChecker allowChecker;
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
    return new OptionData[] { new OptionData(OptionType.STRING, "volume", "Volume level (0-150)", false) };
  }

  @Override
  public void execute(BotCommandContext context) {
    if (checkUserAccess(context.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(context.getUser());
      log.debug("User {} does not have access to use: {}", context.getUser(), commandName);

      return;
    }

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

    int volumeLevel = settings.getVolume();
    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder("**Settings volume**").append(newLine);

    messageContent.append("Current volume: ");
    messageContent.append(volumeLevel);
    messageContent.append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers = new HashMap<>();
    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(volume, this::makeModalVolume);

    modalHandlers = new HashMap<>();
    modalHandlers.put(volume, this::handleModalVolume);

    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName, 30000));
  }

  private void updateVolume(int volumeLevel) {
    playerService.setVolume(volumeLevel);
    log.debug("Current volume level: {}", volumeLevel);
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

  private void makeModalVolume(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(volume, "Set Volume")
        .addActionRow(
            TextInput.create(volume, "Volume Level (0-150)", TextInputStyle.SHORT).setRequiredRange(0, 3).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", volume);
  }

  private void handleModalVolume(ModalInteractionEvent modalEvent) {
    String input = modalEvent.getValue(volume).getAsString();
    int volumeLevel = 100;

    try {
      volumeLevel = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      log.warn("Error parsing volume level from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.warn("Error accessing the first argument for volume level", e);
    }

    updateVolume(volumeLevel);
    modalEvent.reply("Volume level set to: " + volumeLevel).setEphemeral(true).queue();
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