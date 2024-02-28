package halot.nikitazolin.bot.command.slash;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import halot.nikitazolin.bot.util.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
public class HelloCommand extends SlashCommand {

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
  public void execute(SlashCommandRecord info) {
    info.event().replyEmbeds(MessageUtils.createInfoEmbed("Gamarjoba genacvale!").build()).queue();
  }
}
