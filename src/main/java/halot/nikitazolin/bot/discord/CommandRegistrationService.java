package halot.nikitazolin.bot.discord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.CommandCollector;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.jda.JdaMaker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Component
@Scope("singleton")
@Getter
@Slf4j
@RequiredArgsConstructor
public class CommandRegistrationService {

  @Autowired
  private List<BotCommand> allCommands;

  private final JdaMaker jdaMaker;
  private final CommandCollector commandCollector;

  public void addCommands() {
    Optional<JDA> jda = jdaMaker.getJda();

    List<CommandData> commandsToRegistration = preparateCommands(allCommands);
    registerCommands(jda, commandsToRegistration);
  }

  private void registerCommands(Optional<JDA> jda, List<CommandData> commandsToRegistration) {
    jda.ifPresentOrElse(jdaL -> {
      jdaL.updateCommands().addCommands(commandsToRegistration).queue();
      log.info("Commands registered");
    }, () -> {
      log.error("JDA is not present!");
      System.out.println("JDA is not present!");
    });
  }

  private List<CommandData> preparateCommands(List<BotCommand> allCommands) {
    List<CommandData> commandsData = new ArrayList<>();

    for (BotCommand command : allCommands) {
      CommandData commandData = create(command);
      commandsData.add(commandData);
      commandCollector.fillActiveCommand(command);
    }

    return commandsData;
  }

  private CommandData create(BotCommand botCommand) {
    if (botCommand.options().length > 0) {
      log.info("Registering command " + botCommand.name() + " with " + botCommand.options().length + " options!");

      return Commands.slash(botCommand.name(), botCommand.description()).addOptions(botCommand.options()).setContexts(InteractionContextType.GUILD);
    } else {
      log.info("Registering command " + botCommand.name() + " with no options!");

      return Commands.slash(botCommand.name(), botCommand.description()).setContexts(InteractionContextType.GUILD);
    }
  }
}
