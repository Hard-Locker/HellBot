package halot.nikitazolin.bot.command.model;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public record SlashCommandRecord(SlashCommand slashCommand, SlashCommandInteractionEvent event, Member sender, TextChannel textChannel, List<OptionMapping> options) {

}
