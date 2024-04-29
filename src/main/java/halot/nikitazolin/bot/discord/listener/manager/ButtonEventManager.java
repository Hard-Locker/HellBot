package halot.nikitazolin.bot.discord.listener.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ButtonMessageCollector;
import halot.nikitazolin.bot.discord.action.CommandCollector;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.action.model.ButtonMessage;
import halot.nikitazolin.bot.discord.tool.SettingChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class ButtonEventManager {

  private final ButtonMessageCollector buttonMessageCollector;
  private final CommandCollector commandCollector;
  private final SettingChecker settingChecker;

  public void processingEvent(ButtonInteractionEvent buttonEvent) {
    if (!settingChecker.checkAllowedTextChannel(buttonEvent.getChannel().asTextChannel(), buttonEvent.getUser())) {
      buttonEvent.getHook().deleteOriginal().queue();

      return;
    }

    Long originalMessageId = buttonEvent.getMessage().getIdLong();
    ButtonMessage buttonMessage = buttonMessageCollector.findButtonMessage(originalMessageId);

    if (buttonMessage == null) {
      log.debug("Not found target message with button");
      return;
    }

    String commandName = buttonMessage.getCommandName();
    getCommand(commandName).ifPresentOrElse(command -> command.buttonClickProcessing(buttonEvent),
        () -> log.debug("Command not found: {}", commandName));
  }

  private Optional<BotCommand> getCommand(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();

    return commands.stream().filter(command -> command.name().equalsIgnoreCase(commandName)).findFirst();
  }
}
