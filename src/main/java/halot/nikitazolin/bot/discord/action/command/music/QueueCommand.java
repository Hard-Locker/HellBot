package halot.nikitazolin.bot.discord.action.command.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.audio.player.AudioItemContext;
import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.command.music.MusicProvider;
import halot.nikitazolin.bot.localization.action.command.setting.SettingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
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
public class QueueCommand extends BotCommand {

  private final PlayerService playerService;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final AllowChecker allowChecker;
  private final ActionMessageCollector actionMessageCollector;
  private final MusicProvider musicProvider;
  private final SettingProvider settingProvider;

  private final String commandName = "queue";
  private final String close = "close";
  private final String next = "nextPage";
  private final String previous = "previousPage";

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
    return musicProvider.getText("queue.description");
  }

  @Override
  public boolean checkUserAccess(User user) {
    return allowChecker.isNotBanned(user);
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

  // TODO
  @Override
  public void execute(BotCommandContext context) {
    if (checkUserAccess(context.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(context.getUser());
      log.debug("User {} does not have access to use: {}", context.getUser(), commandName);

      return;
    }

//    Button closeButton = Button.danger(close, settingProvider.getText("setting.button.close"));
//    Button nextButton = Button.primary(next, Emoji.fromUnicode("U+2192"));
//    Button previousButton = Button.primary(previous, Emoji.fromUnicode("U+2190"));
//    List<Button> buttons = List.of(closeButton, nextButton, previousButton);
//
//    if (playerService.getQueue().isEmpty() == false) {
//      Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), makeMessage(), buttons);
//      buttonHandlers.put(close, this::selectClose);
////      modalHandlers.put(input, this::handleModalInput);
//      actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName, 300000));
//    } else {
//      EmbedBuilder embed = messageFormatter.createInfoEmbed(musicProvider.getText("queue_command.message.empty"));
//      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
//    }

    if (playerService.getQueue().isEmpty() == false) {
      EmbedBuilder embed = messageFormatter.createInfoEmbed(
          musicProvider.getText("queue.message.size") + ": " + playerService.getQueue().size());
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
    } else {
      EmbedBuilder embed = messageFormatter.createInfoEmbed(musicProvider.getText("queue.message.empty"));
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
    }
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

  private MessageCreateData makeMessage() {
    BlockingQueue<AudioItemContext> queue = playerService.getQueue();

    String newLine = System.lineSeparator();
    StringBuilder messageContent = new StringBuilder("**" + musicProvider.getText("queue.message.title") + "**")
        .append(newLine);

    messageContent.append("Track in queue: " + queue.size()).append(newLine);

    MessageCreateData messageCreateData = new MessageCreateBuilder().setContent(messageContent.toString()).build();

    return messageCreateData;
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