package halot.nikitazolin.bot.discord.action.model;

import java.util.List;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class BotCommand {

  public abstract String name();

  public abstract List<String> nameAliases();

  public abstract List<String> commandPrefixes();

  public abstract String description();

  public abstract boolean checkUserAccess(User user);

  public abstract Permission neededPermission();

  public abstract boolean guildOnly();

  public abstract OptionData[] options();

  public abstract void execute(BotCommandContext context);

  public abstract void buttonClickProcessing(ButtonInteractionEvent buttonEvent);

  public abstract void modalInputProcessing(ModalInteractionEvent modalEvent);
}
