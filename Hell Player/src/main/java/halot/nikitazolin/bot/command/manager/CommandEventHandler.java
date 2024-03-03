package halot.nikitazolin.bot.command.manager;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import halot.nikitazolin.bot.HellBot;
import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import halot.nikitazolin.bot.util.MessageUtil;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class CommandEventHandler extends ListenerAdapter {

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getGuild() == null) {
      return;
    }

    if (event.getUser().isBot()) {
      return;
    }

    if (event.getMember() == null) {
      return;
    }

    Optional<SlashCommand> command = getCommand(event.getName());
    
    if (command.isEmpty()) {
      return;
    }

    if (command.get().neededPermission() != null && !event.getMember().hasPermission(command.get().neededPermission())) {
      event.replyEmbeds(MessageUtil.createErrorEmbed("You don't have the permission to execute this command!").build()).setEphemeral(true).queue();
      
      return;
    }

    SlashCommandRecord record = new SlashCommandRecord(command.get(), event, event.getMember(), event.getChannel().asTextChannel(), event.getOptions());
    command.get().execute(record);
  }
  
//  @Override
//  public void onMessageReceived(MessageReceivedEvent event) {
//    User author = event.getAuthor();
//    MessageChannelUnion channel = event.getChannel();
//    Message message = event.getMessage();
//
//    if (event.isFromGuild()) {
//      System.out.printf("[%s] [%#s] %#s: %s\n", event.getGuild().getName(), channel, author, message.getContentDisplay());
//    } else {
//      System.out.printf("[direct] %#s: %s\n", author, message.getContentDisplay());
//    }
//
//    if (channel.getType() == ChannelType.TEXT) {
//      System.out.println("The channel topic is " + channel.asTextChannel().getTopic());
//    }
//
//    if (channel.getType().isThread()) {
//      System.out.println("This thread is part of channel #" + channel.asThreadChannel().getParentChannel().getName());
//    }
//  }

  private Optional<SlashCommand> getCommand(String name) {
    return HellBot.getCommandRegistry().getActiveCommands().stream().filter(command -> command.name().equalsIgnoreCase(name)).findFirst();
  }
}
