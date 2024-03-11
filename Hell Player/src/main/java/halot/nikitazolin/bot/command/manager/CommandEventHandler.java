package halot.nikitazolin.bot.command.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.CommandArguments;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

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

    List<String> stringArgs = new ArrayList<>();
    List<Integer> integerArgs = new ArrayList<>();
    List<Attachment> attachmentArgs = new ArrayList<>();
    stringArgs = slashEvent.getOptions().stream().filter(option -> option.getType() == OptionType.STRING).map(option -> option.getAsString()).toList();
    integerArgs = slashEvent.getOptions().stream().filter(option -> option.getType() == OptionType.INTEGER).map(option -> option.getAsInt()).toList();
    attachmentArgs = slashEvent.getOptions().stream().filter(option -> option.getType() == OptionType.ATTACHMENT).map(option -> option.getAsAttachment()).toList();
    CommandArguments argumentMapper = new CommandArguments(stringArgs, integerArgs, attachmentArgs);

    BotCommandContext context = new BotCommandContext(command.get(), slashEvent, null, argumentMapper);
    command.get().execute(context);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent messageEvent) {
    if (messageEvent.getAuthor().isBot() || messageEvent.isWebhookMessage()) {
      return;
    }

    Message message = messageEvent.getMessage();
    String[] messageParts = message.getContentRaw().trim().split(" ", 2);
    LinkedList<String> messagePartsList = new LinkedList<>(Arrays.asList(messageParts));
    String commandName = messagePartsList.getFirst();
    messagePartsList.removeFirst();

    System.out.println("message: " + message);
    System.out.println("messageParts: " + Arrays.toString(messageParts));
    System.out.println("commandName: " + commandName);

    Optional<BotCommand> command = getCommandByReceivedMessage(commandName);

    if (command.isEmpty()) {
      return;
    }

    if (command.get().neededPermission() != null && !messageEvent.getMember().hasPermission(command.get().neededPermission())) {
      messageEvent.getChannel().asTextChannel().sendMessage("You don't have the permission to execute this command!").queue();
      return;
    }

    List<String> stringArgs = new ArrayList<>();
    List<Integer> integerArgs = new ArrayList<>();
    List<Attachment> attachmentArgs = new ArrayList<>();
    stringArgs = messagePartsList;
    attachmentArgs = message.getAttachments();
    CommandArguments argumentMapper = new CommandArguments(stringArgs, integerArgs, attachmentArgs);

    BotCommandContext context = new BotCommandContext(command.get(), null, messageEvent, argumentMapper);
    command.get().execute(context);
  }

  private Optional<BotCommand> getCommandByReceivedMessage(String commandName) {
    List<BotCommand> commands = HellBot.getCommandRegistry().getActiveCommands();

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
    return HellBot.getCommandRegistry().getActiveCommands().stream().filter(command -> command.name().equalsIgnoreCase(commandName)).findFirst();
  }
}
