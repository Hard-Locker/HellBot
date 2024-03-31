package halot.nikitazolin.bot.discord.command.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.command.model.BotCommand;
import halot.nikitazolin.bot.discord.command.model.BotCommandContext;
import halot.nikitazolin.bot.discord.command.model.CommandArguments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommandEventHandler extends ListenerAdapter {
  
  private final CommandCollector commandCollector;
  
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
      slashEvent.reply("You don't have the permission to execute this command!").setEphemeral(true).queue();
      return;
    }
    
    slashEvent.deferReply().queue();
    
    List<String> stringArgs = new ArrayList<>();
    List<Attachment> attachmentArgs = new ArrayList<>();
    stringArgs = slashEvent.getOptions().stream().filter(option -> option.getType() == OptionType.STRING).map(option -> option.getAsString()).toList();
    attachmentArgs = slashEvent.getOptions().stream().filter(option -> option.getType() == OptionType.ATTACHMENT).map(option -> option.getAsAttachment()).toList();
    CommandArguments commandArguments = new CommandArguments(stringArgs, attachmentArgs);

    BotCommandContext context = new BotCommandContext(command.get(), slashEvent, null, commandArguments);
    
    slashEvent.getHook().deleteOriginal().queue();
    command.get().execute(context);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent messageEvent) {
    if (messageEvent.getAuthor().isBot() || messageEvent.isWebhookMessage()) {
      return;
    }

    Message message = messageEvent.getMessage();
    String[] messageParts = message.getContentRaw().trim().split(" ", 2);
    List<String> arguments = new ArrayList<>();

    if(messageParts.length > 1) {
      String[] parts = messageParts[1].split("\\n");
      arguments.addAll(Arrays.asList(parts));
    }

    String commandName = messageParts[0];
    Optional<BotCommand> command = getCommandByReceivedMessage(commandName);

    if (command.isEmpty()) {
      return;
    }

    if (command.get().neededPermission() != null && !messageEvent.getMember().hasPermission(command.get().neededPermission())) {
      messageEvent.getChannel().asTextChannel().sendMessage("You don't have the permission to execute this command!").queue();
      return;
    }

    List<String> stringArgs = new ArrayList<>();
    List<Attachment> attachmentArgs = new ArrayList<>();
    stringArgs = arguments;
    attachmentArgs = message.getAttachments();
    CommandArguments commandArguments = new CommandArguments(stringArgs, attachmentArgs);

    BotCommandContext context = new BotCommandContext(command.get(), null, messageEvent, commandArguments);
    command.get().execute(context);
  }

  private Optional<BotCommand> getCommandByReceivedMessage(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();

    for (BotCommand command : commands) {
      List<String> prefixes = command.commandPrefixes();
      List<String> names = command.nameAliases();

      for (String prefix : prefixes) {
        for (String name : names) {

          String fullCommand = prefix + name;

          if (fullCommand.equals(commandName)) {
            log.debug("User call command: " + commandName);
            return Optional.of(command);
          }
        }
      }
    }

    log.debug("Not found command: " + commandName);
    return Optional.empty();
  }

  private Optional<BotCommand> getSlashCommand(String commandName) {
    List<BotCommand> commands = commandCollector.getActiveCommands();
    return commands.stream().filter(command -> command.name().equalsIgnoreCase(commandName)).findFirst();
  }
}
