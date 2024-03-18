package halot.nikitazolin.bot.command.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Getter
@ToString
@EqualsAndHashCode
@Slf4j
public class BotCommandContext {

  private final BotCommand botCommand;
  private final SlashCommandInteractionEvent slashCommandEvent;
  private final MessageReceivedEvent messageReceivedEvent;
  private final CommandArguments argumentMapper;

  private Guild guild;
  private User user;
  private Member member;
  private TextChannel textChannel;

  public BotCommandContext(BotCommand botCommand, SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent, CommandArguments argumentMapper) {
    super();
    this.botCommand = botCommand;
    this.slashCommandEvent = slashCommandEvent;
    this.messageReceivedEvent = messageReceivedEvent;
    this.argumentMapper = argumentMapper;

    guild = fillGuild(slashCommandEvent, messageReceivedEvent);
    user = fillUser(slashCommandEvent, messageReceivedEvent);
    member = fillMember(slashCommandEvent, messageReceivedEvent);
    textChannel = fillTextChannel(slashCommandEvent, messageReceivedEvent);
    
//    System.out.println("");
  }

  public void sendText(CharSequence text) {
    textChannel.sendMessage(text).queue();
  }

  public void sendMessage(MessageCreateData messageCreateData) {
    textChannel.sendMessage(messageCreateData).queue();
  }

  public void sendMessagesEmbed(List<MessageEmbed> messagesEmbed) {
    textChannel.sendMessageEmbeds(messagesEmbed).queue();
  }
  
  public void sendMessageEmbed(EmbedBuilder embedBuilder) {
    MessageCreateData messageCreateData = new MessageCreateBuilder().setEmbeds(embedBuilder.build()).build();

    textChannel.sendMessage(messageCreateData).queue();
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

    log.error("Guild is not found");
    return null;
  }

  private User fillUser(SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent) {
    List<Supplier<User>> userSuppliers = new ArrayList<>();

    if (slashCommandEvent != null) {
      userSuppliers.add(() -> slashCommandEvent.getUser());
    }

    if (messageReceivedEvent != null) {
      userSuppliers.add(() -> messageReceivedEvent.getAuthor());
    }

    for (Supplier<User> supplier : userSuppliers) {
      User user = supplier.get();

      if (user != null) {
        return user;
      }
    }

    log.error("User is not found");
    return null;
  }

  private Member fillMember(SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent) {
    List<Supplier<Member>> memberSuppliers = new ArrayList<>();

    if (slashCommandEvent != null) {
      memberSuppliers.add(() -> slashCommandEvent.getMember());
    }

    if (messageReceivedEvent != null) {
      memberSuppliers.add(() -> messageReceivedEvent.getMember());
    }

    for (Supplier<Member> supplier : memberSuppliers) {
      Member member = supplier.get();

      if (member != null) {
        return member;
      }
    }

    log.error("Member is not found");
    return null;
  }

  private TextChannel fillTextChannel(SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent) {
    List<Supplier<TextChannel>> textChannelSuppliers = new ArrayList<>();

    if (slashCommandEvent != null) {
      textChannelSuppliers.add(() -> slashCommandEvent.getChannel().asTextChannel());
    }

    if (messageReceivedEvent != null) {
      textChannelSuppliers.add(() -> messageReceivedEvent.getChannel().asTextChannel());
    }

    for (Supplier<TextChannel> supplier : textChannelSuppliers) {
      TextChannel textChannel = supplier.get();

      if (textChannel != null) {
        return textChannel;
      }
    }

    log.error("TextChannel is not found");
    return null;
  }
}
