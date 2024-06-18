package halot.nikitazolin.bot.discord.listener.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.CommandCollector;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class StringSelectInteractionEventManager {

  private final ActionMessageCollector actionMessageCollector;
  private final CommandCollector commandCollector;
  private final AllowChecker allowChecker;

  public void processingEvent(StringSelectInteractionEvent selectEvent) {
    if (!allowChecker.isAllowedTextChannel(selectEvent.getChannel().asTextChannel(), selectEvent.getUser())) {
      selectEvent.getHook().deleteOriginal().queue();

      return;
    }

    Long originalMessageId = selectEvent.getMessage().getIdLong();
    ActionMessage actionMessage = actionMessageCollector.findMessage(originalMessageId);

    if (actionMessage == null) {
      log.debug("Not found target message");

      return;
    }

    String commandName = actionMessage.getCommandName();
    getCommand(commandName).ifPresentOrElse(command -> command.stringSelectProcessing(selectEvent),
        () -> log.debug("Command not found: {}", commandName));
  }

  private Optional<BotCommand> getCommand(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();

    return commands.stream().filter(command -> command.name().equalsIgnoreCase(commandName)).findFirst();
  }
}
