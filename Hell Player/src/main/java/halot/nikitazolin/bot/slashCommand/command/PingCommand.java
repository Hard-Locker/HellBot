package halot.nikitazolin.bot.slashCommand.command;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.slashCommand.model.SlashCommand;
import halot.nikitazolin.bot.slashCommand.model.SlashCommandRecord;
import halot.nikitazolin.bot.util.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
public class PingCommand extends SlashCommand {

  @Override
  public String name() {
    return "ping";
  }

  @Override
  public String description() {
    return "Wanna check ping?";
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
    final long time = System.currentTimeMillis();

    info.event().replyEmbeds(MessageUtils.createInfoEmbed("Getting Response Time...").build()).setEphemeral(true)
        .queue(response -> {
          response.editOriginalEmbeds(
              MessageUtils.createSuccessEmbed("Response Time: " + (System.currentTimeMillis() - time) + "ms").build())
              .queue();
        }, failure -> info.event().replyEmbeds(MessageUtils.createErrorEmbed("Failed to get response time!").build())
            .queue());
  }
}
