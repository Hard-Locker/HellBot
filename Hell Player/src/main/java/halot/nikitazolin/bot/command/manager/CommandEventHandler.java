package halot.nikitazolin.bot.command.manager;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Component
@Slf4j
public class CommandEventHandler extends ListenerAdapter {
  
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent slashEvent) {
    if (slashEvent.getGuild() == null || slashEvent.getUser().isBot() || slashEvent.getMember() == null) {
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

    String message = messageEvent.getMessage().getContentRaw();
    String[] messageParts = message.trim().split(" ", 2);
    String commandName = messageParts[0];
    List<OptionMapping> options = new ArrayList<>();

//    System.out.println("message: " + message);
//    System.out.println("messageParts: " + Arrays.toString(messageParts));
//    System.out.println("commandText: " + commandName);

    Optional<BotCommand> command = getCommandByReceivedMessage(commandName);

    if (command.isEmpty()) {
      return;
    }

    if (command.get().neededPermission() != null && !messageEvent.getMember().hasPermission(command.get().neededPermission())) {
      messageEvent.getChannel().asTextChannel().sendMessage("You don't have the permission to execute this command!").queue();
      return;
    }

    BotCommandContext context = new BotCommandContext(command.get(), null, messageEvent, options);
    command.get().execute(context);
  }

  private Optional<BotCommand> getCommandByReceivedMessage(String commandText) {
    List<BotCommand> commands = HellBot.getCommandRegistry().getActiveCommands();

    for (BotCommand command : commands) {
      List<String> prefixes = command.commandPrefixes();
      List<String> names = command.nameAliases();

      for (String prefix : prefixes) {
        for (String name : names) {

          String fullCommand = prefix + name;

          if (fullCommand.equals(commandText)) {
            log.debug("User call command: " + commandText);
            return Optional.of(command);
          }
        }
      }
    }

    log.debug("Not found command: " + commandText);
    return Optional.empty();
  }
  
  private Optional<BotCommand> getSlashCommand(String name) {
    return HellBot.getCommandRegistry().getActiveCommands().stream().filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
  }
}
