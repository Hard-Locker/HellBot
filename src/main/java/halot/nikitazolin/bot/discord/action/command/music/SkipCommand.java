package halot.nikitazolin.bot.discord.action.command.music;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
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

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SkipCommand extends BotCommand {

  private final PlayerService playerService;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final AllowChecker allowChecker;

  private final String commandName = "skip";

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
    return "Skip track. Optionally, you can specify the position of some tracks to remove them";
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
    return new OptionData[] { new OptionData(OptionType.STRING, "positions", "Track positions to skip", false) };
  }

  @Override
  public void execute(BotCommandContext context) {
    if (checkUserAccess(context.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(context.getUser());
      log.debug("User {} does not have access to use: {}", context.getUser(), commandName);

      return;
    }

    if (playerService.getQueue().isEmpty() == true) {
      EmbedBuilder embed = messageFormatter.createWarningEmbed(
          context.getUser().getAsMention() + " This command can be used if playing queue have tracks");
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
      log.debug("User try skip track in empty queue" + context.getUser());

      return;
    }

    List<String> args = context.getCommandArguments().getString();

    if (args.isEmpty()) {
      playerService.skipTrack();
    } else {
      List<Integer> positions = new ArrayList<>();

      for (String element : args) {
        positions.addAll(parsePositions(element));
      }

      playerService.skipTracks(positions);
    }

    EmbedBuilder embed = messageFormatter.createInfoEmbed("Track skiped by user: " + context.getUser().getAsMention());
    messageSender.sendMessageEmbed(context.getTextChannel(), embed);

    log.debug("Track skiped by user: " + context.getUser());
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    return;
  }

  @Override
  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    return;
  }

  private List<Integer> parsePositions(String input) {
    List<Integer> positions = new ArrayList<>();
    Pattern pattern = Pattern.compile("(\\d+)(?:-(\\d+))?");
    Matcher matcher = pattern.matcher(input);

    while (matcher.find()) {
      int start = Integer.parseInt(matcher.group(1));
      String endGroup = matcher.group(2);

      if (endGroup != null) {
        int end = Integer.parseInt(endGroup);

        for (int i = start; i <= end; i++) {
          positions.add(i);
        }
      } else {
        positions.add(start);
      }
    }

    return positions;
  }
}