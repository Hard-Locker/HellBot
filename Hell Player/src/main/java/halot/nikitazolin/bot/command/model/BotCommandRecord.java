package halot.nikitazolin.bot.command.model;

import java.util.List;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Component
public record BotCommandRecord(BotCommand botCommand, SlashCommandInteractionEvent slashCommandEvent, MessageReceivedEvent messageReceivedEvent, List<OptionMapping> options) {

}
