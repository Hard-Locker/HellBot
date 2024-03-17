package halot.nikitazolin.bot.command.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.jda.JdaService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Component
@Lazy
@Scope("singleton")
@Getter
@Slf4j
@RequiredArgsConstructor
public class CommandRegistrator {

  @Autowired
  private List<BotCommand> allCommands;
  
  private final JdaService jdaService;
  private final CommandSaver commandSaver;

  @PostConstruct
  public void init() {
    System.out.println("CommandRegistrator is construct");
    Optional<JDA> jda = jdaService.getJda();

    List<CommandData> commandsToRegistration = preparateCommands(allCommands);
    registerCommands(jda, commandsToRegistration);
  }

  private void registerCommands(Optional<JDA> jda, List<CommandData> commandsToRegistration) {
    jda.ifPresentOrElse(jdaL -> {
      jdaL.updateCommands().addCommands(commandsToRegistration).queue();
    }, () -> System.out.println("JDA is not present!"));
  }

  private List<CommandData> preparateCommands(List<BotCommand> allCommands) {
    List<CommandData> commandsData = new ArrayList<>();

    for (BotCommand command : allCommands) {
      CommandData commandData = create(command);
      commandsData.add(commandData);
      commandSaver.fillActiveCommand(command);
    }
    return commandsData;
  }

  private CommandData create(BotCommand botCommand) {
    if (botCommand.options().length > 0) {
      log.info("Registering command " + botCommand.name() + " with " + botCommand.options().length + " options!");
      
      return Commands.slash(botCommand.name(), botCommand.description()).addOptions(botCommand.options())
          .setGuildOnly(botCommand.guildOnly());
    } else {
      log.warn("Registering command " + botCommand.name() + " with no options!");
      
      return Commands.slash(botCommand.name(), botCommand.description()).setGuildOnly(botCommand.guildOnly());
    }
  }
}
