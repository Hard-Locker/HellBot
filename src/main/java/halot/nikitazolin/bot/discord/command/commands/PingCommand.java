package halot.nikitazolin.bot.discord.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.command.model.BotCommand;
import halot.nikitazolin.bot.discord.command.model.BotCommandContext;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class PingCommand extends BotCommand {

  private final MessageUtil messageUtil;
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
//    final long time = System.currentTimeMillis();
//
//    jda.getRestPing().queue(ping -> System.out.println("Logged in with ping: " + ping));
//
//    context.getSlashCommandEvent()
//      .replyEmbeds(MessageUtil.createInfoEmbed("Getting Response Time...").build())
//      .setEphemeral(true)
//      .queue(response -> {
//          response.editOriginalEmbeds(MessageUtil.createSuccessEmbed("Response Time: " + (System.currentTimeMillis() - time) + "ms").build()).queue();
//        }, failure -> context.getSlashCommandEvent().replyEmbeds(MessageUtil.createErrorEmbed("Failed to get response time!").build()).queue());
  }
}
