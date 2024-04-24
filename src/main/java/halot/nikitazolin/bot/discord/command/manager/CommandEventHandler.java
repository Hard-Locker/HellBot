package halot.nikitazolin.bot.discord.command.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.DatabaseService;
import halot.nikitazolin.bot.discord.command.BotCommandContext;
import halot.nikitazolin.bot.discord.command.model.BotCommand;
import halot.nikitazolin.bot.discord.command.model.CommandArguments;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommandEventHandler extends ListenerAdapter {

  private final CommandCollector commandCollector;
  private final MessageUtil messageUtil;
  private final Settings settings;
  private final DatabaseService databaseService;

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
    
    if (!checkAllowedTextChannel(slashEvent.getChannel().asTextChannel(), slashEvent.getUser())) {
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

    databaseService.saveUserToDb(context.getMember());
    databaseService.saveEventHistoryToDb(context);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent messageEvent) {
    if (messageEvent.getAuthor().isBot() || messageEvent.isWebhookMessage()) {
      return;
    }

    Message message = messageEvent.getMessage();
    String[] messageParts = message.getContentRaw().trim().split(" ", 2);
    List<String> arguments = new ArrayList<>();

    if (messageParts.length > 1) {
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
    
    if (!checkAllowedTextChannel(messageEvent.getChannel().asTextChannel(), messageEvent.getAuthor())) {
      return;
    }

    List<String> stringArgs = new ArrayList<>();
    List<Attachment> attachmentArgs = new ArrayList<>();
    stringArgs = arguments;
    attachmentArgs = message.getAttachments();

    CommandArguments commandArguments = new CommandArguments(stringArgs, attachmentArgs);
    BotCommandContext context = new BotCommandContext(command.get(), null, messageEvent, commandArguments);

    command.get().execute(context);

    databaseService.saveUserToDb(context.getMember());
    databaseService.saveEventHistoryToDb(context);
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
  
  private boolean checkAllowedTextChannel(TextChannel textChannel, User user) {
    List<Long> allowedTextChannelIds = new ArrayList<>();

    try {
      allowedTextChannelIds.addAll(settings.getAllowedTextChannelIds());
    } catch (NullPointerException e) {
      log.debug("Especial allowed text channel not set. Bot can connect read any channel.");

      return true;
    }

    if (allowedTextChannelIds.isEmpty() || allowedTextChannelIds.contains(textChannel.getIdLong())) {
      return true;
    } else {
      EmbedBuilder embed = messageUtil.createErrorEmbed(textChannel.getName() + " text channel is denied for bot.");
      MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embed.build()).build();

      if (!user.isBot()) {
        user.openPrivateChannel().queue((privateChannel) -> {
          privateChannel.sendMessage(messageCreateData).queue();
        }, (error) -> {
          log.warn("Failed to send a private message to the user: " + user);
        });
      }

      log.debug("User tried to use a command in an denied text channel.");

      return false;
    }
  }
}
