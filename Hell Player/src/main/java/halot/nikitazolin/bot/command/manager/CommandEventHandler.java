package halot.nikitazolin.bot.command.manager;

import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
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

    Optional<BotCommand> command = getSlashCommand(slashEvent.getName());
    
    if (command.isEmpty()) {
      return;
    }

    if (command.get().neededPermission() != null && !slashEvent.getMember().hasPermission(command.get().neededPermission())) {
      slashEvent.replyEmbeds(MessageUtil.createErrorEmbed("You don't have the permission to execute this command!").build()).setEphemeral(true).queue();
      
      return;
    }

    BotCommandContext context = new BotCommandContext(command.get(), slashEvent, null, slashEvent.getOptions());
    command.get().execute(context);
  }

  //TODO
  @Override
  public void onMessageReceived(MessageReceivedEvent messageEvent) {
    if (messageEvent.getAuthor().isBot() || messageEvent.isWebhookMessage()) {
      return;
    }

//    String messageText = messageEvent.getMessage().getContentRaw().split("\\s+")[0];
    String messageText = messageEvent.getMessage().getContentRaw();
    System.out.println(messageText);
    
    Optional<BotCommand> command = getMessageCommand(messageText);
//    System.out.println(command.get());
    
    BotCommandContext context = new BotCommandContext(command.get(), null, messageEvent, null);
    command.get().execute(context);
  }

  private Optional<BotCommand> getMessageCommand(String nameAliases) {
    return HellBot.getCommandRegistry().getActiveCommands().stream().filter(command -> command.nameAliases().contains(nameAliases)).findFirst();
  }
  
  private Optional<BotCommand> getSlashCommand(String name) {
    return HellBot.getCommandRegistry().getActiveCommands().stream().filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
  }
}
