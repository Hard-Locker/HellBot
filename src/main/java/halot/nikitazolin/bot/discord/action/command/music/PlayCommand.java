package halot.nikitazolin.bot.discord.action.command.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.audio.loader.InputLinkLoader;
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
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class PlayCommand extends BotCommand {

  private final GuildAudioService guildAudioService;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final ActionMessageCollector actionMessageCollector;
  private final AllowChecker allowChecker;
  private final MusicProvider musicProvider;
  private final SettingProvider settingProvider;
  private final InputLinkLoader inputLinkLoader;

  private final String commandName = "play";
  private final String idYes = "yes";
  private final String idNo = "no";
  private final String idlinks = "links";
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
    return musicProvider.getText("play_command.description");
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
    return new OptionData[] { new OptionData(OptionType.STRING, musicProvider.getText("play_command.input.name"),
        musicProvider.getText("play_command.input.description"), false) };
  }

  @Override
  public void execute(BotCommandContext context) {
    // Checking user permission to be use this command. Exit if denied
    if (checkUserAccess(context.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(context.getUser());
      log.debug("User {} does not have access to use: {}", context.getUser(), commandName);

      return;
    }

    // Checking the validity of the voice channel for connection. Exit if invalid
    if (guildAudioService.connectToVoiceChannel(context) == false) {
      return;
    }

    List<String> inputLinks = context.getCommandArguments().getString();
    List<Attachment> inputAttachments = context.getCommandArguments().getAttachment();
    List<String> preparedUrls = inputLinkLoader.processingInputLinks(inputLinks);
    List<String> preparedAttachments = inputLinkLoader.processingInputAttachments(inputAttachments);

    // Only fill queue
    guildAudioService.getPlayerService().fillQueue(preparedUrls, context);
    guildAudioService.getPlayerService().fillQueue(preparedAttachments, context);

    // Getting a link from the queue and trying to play it
    if (guildAudioService.getPlayerService().getQueue().isEmpty() == false) {
      guildAudioService.getPlayerService().play();

      EmbedBuilder embed = messageFormatter.createSuccessEmbed(
          musicProvider.getText("play_command.message.success") + ": " + context.getUser().getAsMention());
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
    }

    // If input links contains additional tracks, bot will offer to load all
    // additional tracks
    CompletableFuture.runAsync(() -> {
      for (String link : inputLinks) {
        if (inputLinkLoader.isPlaylist(link)) {
          makeMessageWithButton(context, link);
        }
      }
    });

    log.debug("User added links to playing music. " + "User: " + context.getUser());
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
    return;
  }

  @Override
  public void stringSelectProcessing(StringSelectInteractionEvent stringSelectEvent) {
    return;
  }

  private void makeMessageWithButton(BotCommandContext context, String link) {
    log.debug("Preparation a message with proposal to load additional links");

    String title = link + " " + musicProvider.getText("play_command.message.proposal") + "?";
    Map<String, Object> additional = new HashMap<>();
    additional.put(idlinks, List.of(link));

    Button yesButton = Button.primary(idYes, settingProvider.getText("setting.text.yes"));
    Button noButton = Button.danger(idNo, settingProvider.getText("setting.text.no"));
    List<Button> buttons = List.of(yesButton, noButton);

    Long messageId = messageSender.sendMessageWithActionRow(context.getTextChannel(), title, buttons);
    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName, 60000, context, additional));

    buttonHandlers.put(idYes, this::selectYes);
    buttonHandlers.put(idNo, this::selectNo);
    log.debug("Shown proposal to load additional links");
  }

  private void selectYes(ButtonInteractionEvent buttonEvent) {
    log.debug("User select loading additional links");

    buttonEvent.getMessage().delete().queue();
    buttonEvent.reply(musicProvider.getText("play_command.message.adding")).setEphemeral(true).queue();

    inputLinkLoader.loadAdditionalLink(buttonEvent.getMessageIdLong(), idlinks);
  }

  private void selectNo(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply(musicProvider.getText("play_command.message.add_no")).setEphemeral(true).queue();
    buttonEvent.getMessage().delete().queue();
    log.debug("Closed loading additional links");
  }

  private void handleUnknownButton(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply(settingProvider.getText("setting.button.close")).setEphemeral(true).queue();
    log.debug("Clicked unknown button");
  }
}