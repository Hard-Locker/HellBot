package halot.nikitazolin.bot.discord.action.command.music;

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
import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.discord.tool.YoutubeLinkManager;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
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
  private final YoutubeLinkManager youtubeLinkManager;
  private final AllowChecker allowChecker;

  private final String commandName = "play";
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
    return "Start playing music from link";
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
    return new OptionData[] { new OptionData(OptionType.STRING, "link", "URL with content", false) };
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
    List<String> preparedUrls = processingInputLinks(inputLinks);

    // Only adding URL to queue
    guildAudioService.getPlayerService().fillQueue(preparedUrls, context);
    // Getting a URL from the queue and trying to play it
    guildAudioService.getPlayerService().play();

    if (preparedUrls.isEmpty() == false) {
      EmbedBuilder embed = messageFormatter
          .createSuccessEmbed("Audiotrack(s) added by user: " + context.getUser().getAsMention());
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
    }

    // If input links contains YouTube mix/playlist, bot will offer to load all
    // links from playlist.
    checkAndHandlePlaylistLink(context, inputLinks);

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

  private List<String> processingInputLinks(List<String> links) {
    List<String> preparedLinks = new ArrayList<>();

    for (String link : links) {
      if (youtubeLinkManager.isYouTubeUrl(link)) {
        String simpleLink = youtubeLinkManager.extractSimpleUrl(link);
        preparedLinks.add(simpleLink);
        log.debug("Exctracted simlpe URL: {} from link: {}", simpleLink, link);
      } else {
        preparedLinks.add(link);
        log.debug("Added link: {}", link);
      }
    }

    return preparedLinks;
  }

  private void checkAndHandlePlaylistLink(BotCommandContext context, List<String> links) {
    for (String link : links) {
      if (youtubeLinkManager.isYouTubePlaylist(link)) {
        List<String> additionalLinks = youtubeLinkManager.extractVideoLinks(link);
        additionalLinks.remove(0);
        log.debug("Link have playlist. Link: {}", link);

        makeMessageWithButton(context, link, additionalLinks);
      }
    }
  }

  private void makeMessageWithButton(BotCommandContext context, String audioTrack, List<String> additionalAudioTracks) {
    log.debug("Preparation a message with proposal to load additional links");
    String yes = "yes";
    String no = "no";
    String title = audioTrack + " have playlist. Download all tacks?";
    Map<String, Object> additional = new HashMap<>();
    additional.put("links", additionalAudioTracks);

    List<Button> buttons = List.of(Button.primary(yes, "Yes"), Button.danger(no, "No"));
    Long messageId = messageSender.sendMessageWithButtons(context.getTextChannel(), title, buttons);
    actionMessageCollector.addMessage(messageId, new ActionMessage(messageId, commandName, 60000, context, additional));

    buttonHandlers.put(yes, this::selectYes);
    buttonHandlers.put(no, this::selectNo);
    log.debug("Shown proposal to load additional links");
  }

  private void selectYes(ButtonInteractionEvent buttonEvent) {
    log.debug("User select loading additional links");
    ActionMessage actionMessage = actionMessageCollector.findMessage(buttonEvent.getMessageIdLong());

    if (actionMessage != null) {
      BotCommandContext context = actionMessage.getContext();
      Map<String, Object> additional = actionMessage.getAdditionalData();
      List<String> links = new ArrayList<>();
      Object linksObject = additional.get("links");

      if (linksObject instanceof List<?>) {
        List<?> rawList = (List<?>) linksObject;

        for (Object item : rawList) {
          if (item instanceof String) {
            links.add((String) item);
          }
        }
      }

      guildAudioService.getPlayerService().fillQueue(links, context);

      buttonEvent.reply("Additional links loaded").setEphemeral(true).queue();
      buttonEvent.getMessage().delete().queue();
      log.debug("Loading additional links");
    }
  }

  private void selectNo(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply("Additional links NOT loaded").setEphemeral(true).queue();
    buttonEvent.getMessage().delete().queue();
    log.debug("Closed loading additional links");
  }

  private void handleUnknownButton(ButtonInteractionEvent buttonEvent) {
    buttonEvent.reply("Unknown button").setEphemeral(true).queue();
    log.debug("Clicked unknown button");
  }
}