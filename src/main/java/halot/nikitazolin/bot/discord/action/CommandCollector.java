package halot.nikitazolin.bot.discord.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.model.BotCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("singleton")
@Getter
@Slf4j
public class CommandCollector {

  private List<BotCommand> activeCommands = new ArrayList<>();

  public void fillActiveCommand(BotCommand command) {
    activeCommands.add(command);
    
    log.debug("Collected command" + command);
  }
}
