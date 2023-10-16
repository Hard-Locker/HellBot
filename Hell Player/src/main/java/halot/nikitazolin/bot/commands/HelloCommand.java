package halot.nikitazolin.bot.commands;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class HelloCommand extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getName().equals("hello")) {
      event.reply("Gamardjobi").queue();
//      event.reply("Привет, " + event.getUser().getAsMention() + "!").queue();
    }
  }
}
