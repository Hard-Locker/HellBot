package halot.nikitazolin.bot.discord.listener.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.CommandCollector;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.tool.AllowChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class ButtonEventManager {

  private final ActionMessageCollector actionMessageCollector;
  private final CommandCollector commandCollector;
  private final AllowChecker allowChecker;

  public void processingEvent(ButtonInteractionEvent buttonEvent) {
    if (!allowChecker.isAllowedTextChannel(buttonEvent.getChannel().asTextChannel(), buttonEvent.getUser())) {
      buttonEvent.getHook().deleteOriginal().queue();

      return;
    }

    Long originalMessageId = buttonEvent.getMessage().getIdLong();
    ActionMessage actionMessage = actionMessageCollector.findMessage(originalMessageId);

    if (actionMessage == null) {
      log.debug("Not found target message");
      return;
    }

    String commandName = actionMessage.getCommandName();
    getCommand(commandName).ifPresentOrElse(command -> command.buttonClickProcessing(buttonEvent),
        () -> log.debug("Command not found: {}", commandName));
  }

  private Optional<BotCommand> getCommand(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();

    return commands.stream().filter(command -> command.name().equalsIgnoreCase(commandName)).findFirst();
  }
}
