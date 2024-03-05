package halot.nikitazolin.bot.command.manager;

import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandRecord;
import halot.nikitazolin.bot.util.MessageUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class CommandEventHandler extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent slashEvent) {
    if (slashEvent.getGuild() == null) {
      return;
    }

    if (slashEvent.getUser().isBot()) {
      return;
    }

    if (slashEvent.getMember() == null) {
      return;
    }

    Optional<BotCommand> command = getCommand(slashEvent.getName());
    
    if (command.isEmpty()) {
      return;
    }

    if (command.get().neededPermission() != null && !slashEvent.getMember().hasPermission(command.get().neededPermission())) {
      slashEvent.replyEmbeds(MessageUtil.createErrorEmbed("You don't have the permission to execute this command!").build()).setEphemeral(true).queue();
      
      return;
    }

    BotCommandRecord record = new BotCommandRecord(command.get(), slashEvent, null, slashEvent.getOptions());
    command.get().execute(record);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent receivedEvent) {
//    if (event.getAuthor().isBot()) {
//      return;
//    }
//
//    String message = event.getMessage().getContentDisplay();
//
//    if (message.startsWith("!")) {
//      String commandName = message.substring(1).split(" ")[0];
//
//      Optional<SlashCommand> command = getCommand(commandName);
//      if (command.isEmpty()) {
//        return;
//      }
//    }
    
    if (receivedEvent.getAuthor().isBot() || receivedEvent.isWebhookMessage()) {
      return;
    }

    String messageText = receivedEvent.getMessage().getContentRaw().split("\\s+")[0];
    
    HellBot.getCommandRegistry().getActiveCommands().stream()
        .filter(command -> command.name().equalsIgnoreCase(messageText) || command.nameAliases().contains(messageText.toLowerCase()))
        .findFirst()
        .ifPresent(command -> {
            BotCommandRecord record = new BotCommandRecord(command, null, receivedEvent, null);
            command.execute(record);
        });
  }

  private Optional<BotCommand> getCommand(String name) {
    return HellBot.getCommandRegistry().getActiveCommands().stream().filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
  }
}
