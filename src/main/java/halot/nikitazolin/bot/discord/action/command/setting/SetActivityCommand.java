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
import halot.nikitazolin.bot.discord.tool.ActivityManager;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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
public class SetActivityCommand extends BotCommand {

  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final ActivityManager activityManager;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "activity";
  private final String close = "close";
  private final String playing = "playing";
  private final String streaming = "streaming";
  private final String listening = "listening";
  private final String watching = "watching";

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
    return "Set bot activity";
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
    Button playingButton = Button.primary(playing, "Set Playing");
    Button streamingButton = Button.primary(streaming, "Set Streaming");
    Button listeningButton = Button.primary(listening, "Set Listening");
    Button watchingButton = Button.primary(watching, "Set Watching");
    List<Button> buttons = List.of(closeButton, playingButton, streamingButton, listeningButton, watchingButton);

    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), "Which activity need set?",
        buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(playing, this::makeModalPlayingActivity);
    buttonHandlers.put(streaming, this::makeModalStreamingActivity);
    buttonHandlers.put(listening, this::makeModalListeningActivity);
    buttonHandlers.put(watching, this::makeModalWatchingActivity);

    modalHandlers.put(playing, this::handleModalPlayingActivity);
    modalHandlers.put(streaming, this::handleModalStreamingActivity);
    modalHandlers.put(listening, this::handleModalListeningActivity);
    modalHandlers.put(watching, this::handleModalWatchingActivity);

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

  private void makeModalPlayingActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(playing, "Set Playing activity")
        .addActionRow(
            TextInput.create(playing, "Enter game name", TextInputStyle.SHORT).setRequiredRange(0, 100).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", playing);
  }

  private void makeModalStreamingActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(streaming, "Set Streaming activity")
        .addActionRow(
            TextInput.create(streaming, "Enter streaming url", TextInputStyle.SHORT).setRequiredRange(0, 100).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", streaming);
  }

  private void makeModalListeningActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(listening, "Set Listening activity")
        .addActionRow(
            TextInput.create(listening, "Enter song name", TextInputStyle.SHORT).setRequiredRange(0, 100).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", listening);
  }

  private void makeModalWatchingActivity(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(watching, "Set Watching activity")
        .addActionRow(
            TextInput.create(watching, "Enter video name", TextInputStyle.SHORT).setRequiredRange(0, 100).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", watching);
  }

  private void handleModalPlayingActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", playing);
    String input = modalEvent.getValue(playing).getAsString();

    activityManager.setPlaying(input);
    modalEvent.reply("Activity updated").setEphemeral(true).queue();
  }

  private void handleModalStreamingActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", streaming);
    String input = modalEvent.getValue(streaming).getAsString();

    activityManager.setStreaming(input);
    modalEvent.reply("Activity updated").setEphemeral(true).queue();
  }

  private void handleModalListeningActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", listening);
    String input = modalEvent.getValue(listening).getAsString();

    activityManager.setListening(input);
    modalEvent.reply("Activity updated").setEphemeral(true).queue();
  }

  private void handleModalWatchingActivity(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", watching);
    String input = modalEvent.getValue(watching).getAsString();

    activityManager.setWatching(input);
    modalEvent.reply("Activity updated").setEphemeral(true).queue();
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
