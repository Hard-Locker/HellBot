package halot.nikitazolin.bot.slashCommand.model;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public abstract class SlashCommand {

    public abstract String name();
    public abstract String description();
    public abstract Permission neededPermission();
    public abstract boolean guildOnly();
    public abstract OptionData[] options();
    public abstract void execute(SlashCommandRecord info);
}
