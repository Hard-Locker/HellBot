package halot.nikitazolin.bot.command.manager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import halot.nikitazolin.bot.util.MessageUtils;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class SlashCommandHandler extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getGuild() == null)
      return;
    if (event.getUser().isBot())
      return;
    if (event.getMember() == null)
      return;

    Optional<SlashCommand> command = getCommand(event.getName());
    if (command.isEmpty())
      return;

    if (command.get().neededPermission() != null && !event.getMember().hasPermission(command.get().neededPermission())) {
      event.replyEmbeds(MessageUtils.createErrorEmbed("You don't have the permission to execute this command!").build()).setEphemeral(true).queue();
      return;
    }

    SlashCommandRecord record = new SlashCommandRecord(command.get(), event, event.getMember(), event.getChannel().asTextChannel(), event.getOptions());
    command.get().execute(record);
  }

  private Optional<SlashCommand> getCommand(String name) {
    return HellBot.getInstance()
        .getCommandRegistry()
        .getActiveCommands()
        .stream().filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
  }
}
