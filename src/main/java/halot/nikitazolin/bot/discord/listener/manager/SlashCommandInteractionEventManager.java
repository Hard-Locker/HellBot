package halot.nikitazolin.bot.discord.listener.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.DatabaseService;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.CommandCollector;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.action.model.CommandArguments;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Component
@Slf4j
@RequiredArgsConstructor
public class SlashCommandInteractionEventManager {

  private final CommandCollector commandCollector;
  private final AllowChecker allowChecker;
  private final DatabaseService databaseService;

  public void processingEvent(SlashCommandInteractionEvent slashEvent) {
    slashEvent.deferReply().queue();

    if (slashEvent.getGuild() == null || slashEvent.getUser().isBot() || slashEvent.getMember() == null) {
      slashEvent.getHook().deleteOriginal().queue();

      return;
    }

    Optional<BotCommand> command = getSlashCommand(slashEvent.getName());

    if (command.isEmpty()) {
      slashEvent.getHook().deleteOriginal().queue();

      return;
    }

    if (command.get().neededPermission() != null && !slashEvent.getMember().hasPermission(command.get().neededPermission())) {
      slashEvent.reply("You don't have the permission to execute this command!").setEphemeral(true).queue();
      slashEvent.getHook().deleteOriginal().queue();

      return;
    }

    if (!allowChecker.checkAllowedTextChannel(slashEvent.getChannel().asTextChannel(), slashEvent.getUser())) {
      slashEvent.getHook().deleteOriginal().queue();

      return;
    }

    List<String> stringArgs = new ArrayList<>();
    List<Attachment> attachmentArgs = new ArrayList<>();
    stringArgs = slashEvent.getOptions().stream().filter(option -> option.getType() == OptionType.STRING).map(option -> option.getAsString()).toList();
    attachmentArgs = slashEvent.getOptions().stream().filter(option -> option.getType() == OptionType.ATTACHMENT).map(option -> option.getAsAttachment()).toList();

    CommandArguments commandArguments = new CommandArguments(stringArgs, attachmentArgs);
    BotCommandContext context = new BotCommandContext(command.get(), slashEvent, null, commandArguments);

    slashEvent.getHook().deleteOriginal().queue();
    log.debug("Call slash command");
    command.get().execute(context);

    databaseService.saveUserToDb(context.getMember());
    databaseService.saveEventHistoryToDb(context);
  }

  private Optional<BotCommand> getSlashCommand(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();

    return commands.stream().filter(command -> command.name().equalsIgnoreCase(commandName)).findFirst();
  }
}
