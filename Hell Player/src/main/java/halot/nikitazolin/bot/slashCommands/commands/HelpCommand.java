package halot.nikitazolin.bot.slashCommands.commands;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.slashCommands.manager.SlashCommand;
import halot.nikitazolin.bot.slashCommands.status.SlashCommandRecord;
import halot.nikitazolin.bot.util.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
public class HelpCommand extends SlashCommand {

  @Override
  public String name() {
    return "help";
  }

  @Override
  public String description() {
    return "Need help?";
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
