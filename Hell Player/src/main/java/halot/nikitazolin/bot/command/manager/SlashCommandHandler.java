package halot.nikitazolin.bot.command.manager;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import halot.nikitazolin.bot.HellPlayer;
import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import halot.nikitazolin.bot.util.MessageUtils;

import java.util.Optional;

public class SlashCommandHandler extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
    if (e.getGuild() == null)
      return;
    if (e.getUser().isBot())
      return;
    if (e.getMember() == null)
      return;

    Optional<SlashCommand> command = getCommand(e.getName());
    if (command.isEmpty())
      return;

    if (command.get().neededPermission() != null && !e.getMember().hasPermission(command.get().neededPermission())) {
      e.replyEmbeds(MessageUtils.createErrorEmbed("You don't have the permission to execute this command!").build())
          .setEphemeral(true).queue();
      return;
    }

    SlashCommandRecord record = new SlashCommandRecord(command.get(), e, e.getMember(), e.getChannel().asTextChannel(),
        e.getOptions());
    command.get().execute(record);
  }

  private Optional<SlashCommand> getCommand(String name) {
    return HellPlayer.getInstance().getCommandRegistry().getActiveCommands().stream()
        .filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
  }
}
