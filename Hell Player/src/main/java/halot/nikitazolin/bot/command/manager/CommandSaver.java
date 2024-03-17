package halot.nikitazolin.bot.command.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.model.BotCommand;
import lombok.Getter;

@Component
@Lazy
@Scope("singleton")
@Getter
//@Slf4j
public class CommandSaver {

  private List<BotCommand> activeCommands = new ArrayList<>();

  public void fillActiveCommand(BotCommand command) {
    activeCommands.add(command);
  }
}
