package halot.nikitazolin.bot.command.model;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Component
@Getter
@ToString
@EqualsAndHashCode
public class BotCommandContext {

  private final BotCommand botCommand;
  private final SlashCommandInteractionEvent slashCommandEvent;
  private final MessageReceivedEvent messageReceivedEvent;
  private final List<OptionMapping> options;
  
  private Guild guild;
  private User user;
  
  public BotCommandContext(BotCommand botCommand, SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent, List<OptionMapping> options) {
    super();
    this.botCommand = botCommand;
    this.slashCommandEvent = slashCommandEvent;
    this.messageReceivedEvent = messageReceivedEvent;
    this.options = options;
    
    guild = fillGuild(slashCommandEvent, messageReceivedEvent);
  }
  
  private Guild fillGuild(SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent) {
    Guild guild = null;

    if (slashCommandEvent.getGuild() != null) {
//      System.out.println("slash: " + slashCommandEvent.getGuild());
      guild = slashCommandEvent.getGuild();
//      System.out.println(guild);
    } else if (messageReceivedEvent.getGuild() != null) {
//      System.out.println("message: " + messageReceivedEvent.getGuild());
      guild = messageReceivedEvent.getGuild();
//      System.out.println(guild);
    }

    return guild;
  }

}
