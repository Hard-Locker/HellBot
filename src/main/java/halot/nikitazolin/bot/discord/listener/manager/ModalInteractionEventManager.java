package halot.nikitazolin.bot.discord.listener.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.CommandCollector;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.tool.SettingChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModalInteractionEventManager {

  private final ActionMessageCollector actionMessageCollector;
  private final CommandCollector commandCollector;
  private final SettingChecker settingChecker;

  public void processingEvent(ModalInteractionEvent modalEvent) {
    if (!settingChecker.checkAllowedTextChannel(modalEvent.getChannel().asTextChannel(), modalEvent.getUser())) {
      modalEvent.getHook().deleteOriginal().queue();

      return;
    }

    Long originalMessageId = modalEvent.getMessage().getIdLong();
    ActionMessage actionMessage = actionMessageCollector.findMessage(originalMessageId);

    if (actionMessage == null) {
      log.debug("Not found target message");
      
      return;
    }

    String commandName = actionMessage.getCommandName();
    getCommand(commandName).ifPresentOrElse(command -> command.modalInputProcessing(modalEvent),
        () -> log.debug("Command not found: {}", commandName));
  }

  private Optional<BotCommand> getCommand(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();

    return commands.stream().filter(command -> command.name().equalsIgnoreCase(commandName)).findFirst();
  }
}