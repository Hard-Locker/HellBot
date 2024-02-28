package halot.nikitazolin.bot.command.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.slash.HelloCommand;
import halot.nikitazolin.bot.command.slash.PingCommand;
import halot.nikitazolin.bot.command.slash.PlayCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Service
@Getter
@Slf4j
public class CommandRegistry {

  private final Optional<JDA> jda = HellBot.getInstance().getJdaService().getJda();
  private List<SlashCommand> activeCommands = new ArrayList<>();
  
  public CommandRegistry() {
    registerCommands();
  }

  private void registerCommands() {
    jda.ifPresentOrElse(jda -> {
      jda.updateCommands()
        .addCommands(
            create(new HelloCommand()), 
            create(new PingCommand()), 
            create(new PlayCommand())
            )
        .queue();
    }, () -> System.out.println("JDA is not present!"));
  }

  private CommandData create(SlashCommand command) {
    this.activeCommands.add(command);
    
    if (command.options().length > 0) {
      log.info("Registering command " + command.name() + " with " + command.options().length + " options!");
      
      return Commands.slash(command.name(), command.description()).addOptions(command.options()).setGuildOnly(command.guildOnly());
    } else {
      log.warn("Registering command " + command.name() + " with no options!");
      
      return Commands.slash(command.name(), command.description()).setGuildOnly(command.guildOnly());
    }
  }
}
