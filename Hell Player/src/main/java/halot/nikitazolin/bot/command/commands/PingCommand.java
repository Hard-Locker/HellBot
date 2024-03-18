package halot.nikitazolin.bot.command.commands;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
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

  @Override
  public String name() {
    return "ping";
  }
  
  @Override
  public List<String> nameAliases() {
    return List.of("ping");
  }

  @Override
  public List<String> commandPrefixes() {
    return List.of("!", "1");
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
//    context.getSlashCommandEvent()
//      .replyEmbeds(MessageUtil.createInfoEmbed("Getting Response Time...").build())
//      .setEphemeral(true)
//      .queue(response -> {
//          response.editOriginalEmbeds(MessageUtil.createSuccessEmbed("Response Time: " + (System.currentTimeMillis() - time) + "ms").build()).queue();
//        }, failure -> context.getSlashCommandEvent().replyEmbeds(MessageUtil.createErrorEmbed("Failed to get response time!").build()).queue());
  }
}
