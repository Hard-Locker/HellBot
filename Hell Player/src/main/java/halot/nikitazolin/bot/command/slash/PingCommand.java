package halot.nikitazolin.bot.command.slash;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandRecord;
import halot.nikitazolin.bot.util.MessageUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
public class PingCommand extends BotCommand {

  @Override
  public String name() {
    return "ping";
  }

  @Override
  public String description() {
    return "Wanna check ping?";
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
    final long time = System.currentTimeMillis();

    info.slashCommandEvent().replyEmbeds(MessageUtil.createInfoEmbed("Getting Response Time...").build()).setEphemeral(true)
        .queue(response -> {
          response.editOriginalEmbeds(MessageUtil.createSuccessEmbed("Response Time: " + (System.currentTimeMillis() - time) + "ms").build()).queue();
        }, failure -> info.slashCommandEvent().replyEmbeds(MessageUtil.createErrorEmbed("Failed to get response time!").build()).queue());
  }
}
