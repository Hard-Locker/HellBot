package halot.nikitazolin.bot.discord.listener.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.DatabaseService;
import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.CommandCollector;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.action.model.CommandArguments;
import halot.nikitazolin.bot.discord.tool.SettingChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageReceivedEventManager {

  private final CommandCollector commandCollector;
  private final SettingChecker settingChecker;
  private final DatabaseService databaseService;

  public void processingEvent(MessageReceivedEvent messageEvent) {
    if (messageEvent.getAuthor().isBot() || messageEvent.isWebhookMessage()) {
      return;
    }

    Message message = messageEvent.getMessage();
    String[] messageParts = message.getContentRaw().trim().split(" ", 2);
    List<String> arguments = new ArrayList<>();

    if (messageParts.length > 1) {
      String[] parts = messageParts[1].split("\\n");
      arguments.addAll(Arrays.asList(parts));
    }

    String commandName = messageParts[0];
    Optional<BotCommand> command = getCommandByReceivedMessage(commandName);

    if (command.isEmpty()) {
      return;
    }

    if (command.get().neededPermission() != null
        && !messageEvent.getMember().hasPermission(command.get().neededPermission())) {
      messageEvent.getChannel().asTextChannel().sendMessage("You don't have the permission to execute this command!")
          .queue();

      return;
    }

    if (!settingChecker.checkAllowedTextChannel(messageEvent.getChannel().asTextChannel(), messageEvent.getAuthor())) {
      return;
    }

    List<String> stringArgs = new ArrayList<>();
    List<Attachment> attachmentArgs = new ArrayList<>();
    stringArgs = arguments;
    attachmentArgs = message.getAttachments();

    CommandArguments commandArguments = new CommandArguments(stringArgs, attachmentArgs);
    BotCommandContext context = new BotCommandContext(command.get(), null, messageEvent, commandArguments);

    command.get().execute(context);

    databaseService.saveUserToDb(context.getMember());
    databaseService.saveEventHistoryToDb(context);
  }

  private Optional<BotCommand> getCommandByReceivedMessage(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();

    for (BotCommand command : commands) {
      List<String> prefixes = command.commandPrefixes();
      List<String> names = command.nameAliases();

      for (String prefix : prefixes) {
        for (String name : names) {

          String fullCommand = prefix + name;

          if (fullCommand.equals(commandName)) {
            log.debug("User call command: " + commandName);
            return Optional.of(command);
          }
        }
      }
    }

    log.debug("Not found command: " + commandName);
    return Optional.empty();
  }
}
