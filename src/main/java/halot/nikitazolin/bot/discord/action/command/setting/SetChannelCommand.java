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
import halot.nikitazolin.bot.discord.tool.DiscordDataReceiver;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.manager.SettingsSaver;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
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
public class SetChannelCommand extends BotCommand {

  private final MessageSender messageSender;
  private final Settings settings;
  private final SettingsSaver settingsSaver;
  private final DiscordDataReceiver discordDataReceiver;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;

  private final String commandName = "channel";
  private final String close = "close";
  private final String addTextChannel = "addTextChannel";
  private final String removeTextChannel = "removeTextChannel";
  private final String addVoiceChannel = "addVoiceChannel";
  private final String removeVoiceChannel = "removeVoiceChannel";

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
    return "Set allowed channels";
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

    Button closeButton = Button.danger(close, "Close settings");
    Button addTextChannelButton = Button.primary(addTextChannel, "Add text channel");
    Button removeTextChannelButton = Button.primary(removeTextChannel, "Remove text channel");
    Button addVoiceChannelButton = Button.primary(addVoiceChannel, "Add voice channel");
    Button removeVoiceChannelButton = Button.primary(removeVoiceChannel, "Remove voice channel");
    List<Button> buttons = List.of(closeButton, addTextChannelButton, removeTextChannelButton, addVoiceChannelButton,
        removeVoiceChannelButton);

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder("**Settings allowed channels**").append(newLine);

    if (settings.getAllowedTextChannelIds() != null && !settings.getAllowedTextChannelIds().isEmpty()) {
      messageContent.append("Current allowed text channels:").append(newLine);
      List<TextChannel> textChannels = discordDataReceiver.getTextChannelsByIds(settings.getAllowedTextChannelIds());

      for (TextChannel textChannel : textChannels) {
        messageContent.append(textChannel.getAsMention());
        messageContent.append(" ID: ");
        messageContent.append(textChannel.getIdLong());
        messageContent.append(newLine);
      }
    }

    messageContent.append(newLine);

    if (settings.getAllowedVoiceChannelIds() != null && !settings.getAllowedVoiceChannelIds().isEmpty()) {
      messageContent.append("Current allowed voice channels:").append(newLine);
      List<VoiceChannel> voiceChannels = discordDataReceiver
          .getVoiceChannelsByIds(settings.getAllowedVoiceChannelIds());

      for (VoiceChannel voiceChannel : voiceChannels) {
        messageContent.append(voiceChannel.getAsMention());
        messageContent.append(" ID: ");
        messageContent.append(voiceChannel.getIdLong());
        messageContent.append(newLine);
      }
    }

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), messageCreateData, buttons);

    buttonHandlers.put(close, this::selectClose);
    buttonHandlers.put(addTextChannel, this::makeModalAddTextChannel);
    buttonHandlers.put(removeTextChannel, this::makeModalRemoveTextChannel);
    buttonHandlers.put(addVoiceChannel, this::makeModalAddVoiceChannel);
    buttonHandlers.put(removeVoiceChannel, this::makeModalRemoveVoiceChannel);

    modalHandlers.put(addTextChannel, this::handleModalAddTextChannel);
    modalHandlers.put(removeTextChannel, this::handleModalRemoveTextChannel);
    modalHandlers.put(addVoiceChannel, this::handleModalAddVoiceChannel);
    modalHandlers.put(removeVoiceChannel, this::handleModalRemoveVoiceChannel);

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

  private void makeModalAddTextChannel(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(addTextChannel, "Add allowed text channel")
        .addActionRow(
            TextInput.create(addTextChannel, "Enter channel ID", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", addTextChannel);
  }

  private void makeModalRemoveTextChannel(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal
        .create(removeTextChannel, "Remove allowed text channel").addActionRow(TextInput
            .create(removeTextChannel, "Enter channel ID", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", removeTextChannel);
  }

  private void makeModalAddVoiceChannel(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal.create(addVoiceChannel, "Add allowed voice channel")
        .addActionRow(
            TextInput.create(addVoiceChannel, "Enter channel ID", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", addVoiceChannel);
  }

  private void makeModalRemoveVoiceChannel(ButtonInteractionEvent buttonEvent) {
    Modal modal = Modal
        .create(removeVoiceChannel, "Remove allowed voice channel").addActionRow(TextInput
            .create(removeVoiceChannel, "Enter channel ID", TextInputStyle.SHORT).setRequiredRange(0, 20).build())
        .build();

    buttonEvent.replyModal(modal).queue();
    log.debug("Opened {} modal", removeVoiceChannel);
  }

  private void handleModalAddTextChannel(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", addTextChannel);
    String input = modalEvent.getValue(addTextChannel).getAsString();
    Long channelId = null;

    try {
      channelId = Long.parseLong(input);
    } catch (NumberFormatException e) {
      log.debug("Error parsing channel ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for channel ID", e);
    }

    TextChannel textChannel = discordDataReceiver.getTextChannelById(channelId);

    if (textChannel != null && settings.getAllowedTextChannelIds() != null) {
      if (!settings.getAllowedTextChannelIds().contains(channelId)) {
        settings.getAllowedTextChannelIds().add(channelId);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

        modalEvent.reply(textChannel.getAsMention() + " has been added").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Text channel has already been added to the list").setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply("Text channel not found").setEphemeral(true).queue();
    }
  }

  private void handleModalRemoveTextChannel(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", removeTextChannel);
    String input = modalEvent.getValue(removeTextChannel).getAsString();
    Long channelId = null;

    try {
      channelId = Long.parseLong(input);
    } catch (NumberFormatException e) {
      log.debug("Error parsing channel ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for channel ID", e);
    }

    if (settings.getAllowedTextChannelIds() != null && settings.getAllowedTextChannelIds().contains(channelId)) {
      settings.getAllowedTextChannelIds().remove(channelId);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      TextChannel textChannel = discordDataReceiver.getTextChannelById(channelId);

      if (textChannel != null) {
        modalEvent.reply(textChannel.getAsMention() + " has been removed from this list").setEphemeral(true).queue();
      } else {
        modalEvent.reply(channelId + " has been removed from this list").setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply("Text channel not found in this list").setEphemeral(true).queue();
    }
  }

  private void handleModalAddVoiceChannel(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", addVoiceChannel);
    String input = modalEvent.getValue(addVoiceChannel).getAsString();
    Long channelId = null;

    try {
      channelId = Long.parseLong(input);
    } catch (NumberFormatException e) {
      log.debug("Error parsing channel ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for channel ID", e);
    }

    VoiceChannel voiceChannel = discordDataReceiver.getVoiceChannelById(channelId);

    if (voiceChannel != null && settings.getAllowedVoiceChannelIds() != null) {
      if (!settings.getAllowedVoiceChannelIds().contains(channelId)) {
        settings.getAllowedVoiceChannelIds().add(channelId);
        settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

        modalEvent.reply(voiceChannel.getAsMention() + " has been added").setEphemeral(true).queue();
      } else {
        modalEvent.reply("Voice channel has already been added to the list").setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply("Voice channel not found").setEphemeral(true).queue();
    }
  }

  private void handleModalRemoveVoiceChannel(ModalInteractionEvent modalEvent) {
    log.debug("Processing modal: {}", removeVoiceChannel);
    String input = modalEvent.getValue(removeVoiceChannel).getAsString();
    Long channelId = null;

    try {
      channelId = Long.parseLong(input);
    } catch (NumberFormatException e) {
      log.debug("Error parsing channel ID from arguments", e);
    } catch (IndexOutOfBoundsException e) {
      log.debug("Error accessing the first argument for channel ID", e);
    }

    if (settings.getAllowedVoiceChannelIds() != null && settings.getAllowedVoiceChannelIds().contains(channelId)) {
      settings.getAllowedVoiceChannelIds().remove(channelId);
      settingsSaver.saveToFile(ApplicationRunnerImpl.SETTINGS_FILE_PATH);

      VoiceChannel voiceChannel = discordDataReceiver.getVoiceChannelById(channelId);

      if (voiceChannel != null) {
        modalEvent.reply(voiceChannel.getAsMention() + " has been removed from this list").setEphemeral(true).queue();
      } else {
        modalEvent.reply(channelId + " has been removed from this list").setEphemeral(true).queue();
      }
    } else {
      modalEvent.reply("Voice channel not found in this list").setEphemeral(true).queue();
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
