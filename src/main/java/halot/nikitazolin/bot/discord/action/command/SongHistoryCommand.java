package halot.nikitazolin.bot.discord.action.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.DatabaseService;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.command.music.MusicProvider;
import halot.nikitazolin.bot.repository.model.SongHistory;
import halot.nikitazolin.bot.util.TimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class SongHistoryCommand extends BotCommand {

  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final DatabaseService databaseService;
  private final TimeConverter timeConverter;
  private final AllowChecker allowChecker;
  private final MusicProvider musicProvider;

  private final String commandName = "history";

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
    return "Show music playing history";
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

  @Override
  public void execute(BotCommandContext context) {
    if (checkUserAccess(context.getUser()) == false) {
      messageSender.sendPrivateMessageAccessError(context.getUser());
      log.debug("User {} does not have access to use: {}", context.getUser(), commandName);

      return;
    }

    String newLine = System.lineSeparator();
    LocalDate today = LocalDate.now();
    List<SongHistory> songs = databaseService.getSongHistoryByDate(today);

    if (songs.isEmpty()) {
      EmbedBuilder embed = messageFormatter.createInfoEmbed("No songs were played today.");
      messageSender.sendMessageEmbed(context.getTextChannel(), embed);

      return;
    }

    EmbedBuilder embed = messageFormatter.createInfoEmbed("Here is your music history for today:");

    for (SongHistory song : songs) {
      String songInfo = String.format("%s - %s" + newLine + "Duration: %s" + newLine + "URL: %s", song.getSongArtist(),
          song.getSongName(), timeConverter.convertLongTimeToSimpleFormat(song.getSongDuration()), song.getSongUrl());

      embed.addField(song.getEventDatetime().format(DateTimeFormatter.ofPattern("HH:mm")), songInfo, false);
    }

    messageSender.sendMessageEmbed(context.getTextChannel(), embed);
    log.debug("Music history sent by user: {}", context.getUser());
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    return;
  }

  @Override
  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    return;
  }
}
