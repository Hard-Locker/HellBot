package halot.nikitazolin.bot.discord.action.command;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.jda.JdaMaker;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.init.settings.model.Settings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class PingCommand extends BotCommand {

  private final JdaMaker jdaMaker;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;

  private final String commandName = "ping";

  @Override
  public String name() {
    return commandName;
  }

  @Override
  public List<String> nameAliases() {
    List<String> defaultAliases = List.of(commandName);
    List<String> additionalAliases = settings.getNameAliases().getOrDefault(commandName, List.of());
    List<String> allAliases = new ArrayList<>(defaultAliases);
    allAliases.addAll(additionalAliases);

    return allAliases;
  }

  @Override
  public List<String> commandPrefixes() {
    List<String> prefixes = new ArrayList<>(List.of("!"));
    List<String> additionalPrefixes = settings.getPrefixes() != null ? settings.getPrefixes() : List.of();
    prefixes.addAll(additionalPrefixes);

    return prefixes;
  }

  @Override
  public String description() {
    return "Wanna check ping?";
  }

  @Override
  public String requiredRole() {
    return null;
  }

  @Override
  public Permission neededPermission() {
    return Permission.USE_APPLICATION_COMMANDS;
  }

  @Override
  public boolean guildOnly() {
    return true;
  }

  @Override
  public OptionData[] options() {
    return new OptionData[] {};
  }

  @Override
  public void execute(BotCommandContext context) {
    final long time = System.currentTimeMillis();

    jdaMaker.getJda().ifPresent(jda -> {
      jda.getRestPing().queue(ping -> {
        long latency = System.currentTimeMillis() - time;
        String pingInfo = String.format("Ping: %d ms (REST API), Latency: %d ms", ping, latency);
        log.trace("User check ping. {}", pingInfo);

        EmbedBuilder embed = messageFormatter.createSuccessEmbed(pingInfo);
        messageSender.sendMessageEmbed(context.getTextChannel(), embed);
      });
    });
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {

  }

  @Override
  public void modalInputProcessing(ModalInteractionEvent modalEvent) {

  }
}
