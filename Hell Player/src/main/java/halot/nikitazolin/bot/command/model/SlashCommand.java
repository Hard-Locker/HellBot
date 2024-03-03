package halot.nikitazolin.bot.command.model;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
public abstract class SlashCommand extends ListenerAdapter {

  public abstract String name();

  public abstract String description();

  public abstract String requiredRole();

  public abstract Permission neededPermission();

  public abstract boolean guildOnly();

  public abstract OptionData[] options();

  public abstract void execute(SlashCommandRecord info);
}
