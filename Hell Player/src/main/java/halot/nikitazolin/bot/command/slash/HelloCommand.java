package halot.nikitazolin.bot.command.slash;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandRecord;
import halot.nikitazolin.bot.util.MessageUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
public class HelloCommand extends BotCommand {

  @Override
  public String name() {
    return "hello";
  }

  @Override
  public String description() {
    return "Greetings";
  }
  
  @Override
  public String requiredRole() {
    return null;
  }

  @Override
  public Permission neededPermission() {
    return Permission.USE_APPLICATION_COMMANDS;
  }

  @Override
  public boolean guildOnly() {
    return true;
  }

  @Override
  public OptionData[] options() {
    return new OptionData[] {};
  }

  @Override
  public void execute(BotCommandRecord info) {
    info.slashCommandEvent().replyEmbeds(MessageUtil.createInfoEmbed("Gamarjoba genacvale!").build()).queue();
  }
}
