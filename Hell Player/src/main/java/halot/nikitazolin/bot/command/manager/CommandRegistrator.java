package halot.nikitazolin.bot.command.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.commands.HelloCommand;
import halot.nikitazolin.bot.command.commands.PingCommand;
import halot.nikitazolin.bot.command.commands.RebootCommand;
import halot.nikitazolin.bot.command.commands.ShutdownCommand;
import halot.nikitazolin.bot.command.commands.music.PlayCommand;
import halot.nikitazolin.bot.command.commands.music.StopCommand;
import halot.nikitazolin.bot.command.model.BotCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Service
@Getter
@Slf4j
public class CommandRegistrator {

  private final Optional<JDA> jda = HellBot.getJdaService().getJda();
  private List<BotCommand> activeCommands = new ArrayList<>();
  private List<CommandData> commandsToRegistration = new ArrayList<>();

  public CommandRegistrator() {
    commandsToRegistration = preparateCommands();
    registerCommands();
  }

  private void registerCommands() {
    jda.ifPresentOrElse(jda -> {
      jda.updateCommands()
        .addCommands(commandsToRegistration)
        .queue();
    }, () -> System.out.println("JDA is not present!"));
  }

  private List<CommandData> preparateCommands() {
    List<CommandData> commands = new ArrayList<>();
    
    commands.add(create(new HelloCommand()));
    commands.add(create(new PingCommand()));
    commands.add(create(new PlayCommand()));
    commands.add(create(new StopCommand()));
    commands.add(create(new RebootCommand()));
    commands.add(create(new ShutdownCommand()));
    
    return commands;
  }
  
  private CommandData create(BotCommand botCommand) {
    this.activeCommands.add(botCommand);
    
    if (botCommand.options().length > 0) {
      log.info("Registering command " + botCommand.name() + " with " + botCommand.options().length + " options!");
      
      return Commands.slash(botCommand.name(), botCommand.description()).addOptions(botCommand.options()).setGuildOnly(botCommand.guildOnly());
    } else {
      log.warn("Registering command " + botCommand.name() + " with no options!");
      
      return Commands.slash(botCommand.name(), botCommand.description()).setGuildOnly(botCommand.guildOnly());
    }
  }
}
