package halot.nikitazolin.bot.command.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
  private Member member;

  public BotCommandContext(BotCommand botCommand, SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent, List<OptionMapping> options) {
    super();
    this.botCommand = botCommand;
    this.slashCommandEvent = slashCommandEvent;
    this.messageReceivedEvent = messageReceivedEvent;
    this.options = options;

    guild = fillGuild(slashCommandEvent, messageReceivedEvent);
  }
  
  public void messageSender() {
    
  }

  private Guild fillGuild(SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent) {
    List<Supplier<Guild>> guildSuppliers = new ArrayList<>();

    if (slashCommandEvent != null) {
      guildSuppliers.add(() -> slashCommandEvent.getGuild());
    }
    
    if (messageReceivedEvent != null) {
      guildSuppliers.add(() -> messageReceivedEvent.getGuild());
    }

    for (Supplier<Guild> supplier : guildSuppliers) {
      Guild guild = supplier.get();

      if (guild != null) {
        return guild;
      }
    }

    return null;
  }

}
