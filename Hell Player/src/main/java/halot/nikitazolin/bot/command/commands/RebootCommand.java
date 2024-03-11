package halot.nikitazolin.bot.command.commands;

import java.util.List;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.audio.BotAudioService;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Slf4j
public class RebootCommand extends BotCommand {

  @Override
  public String name() {
    return "reboot";
  }
  
  @Override
  public List<String> nameAliases() {
    return List.of("reboot");
  }

  @Override
  public List<String> commandPrefixes() {
    return List.of("!", "1");
  }
  
  @Override
  public String description() {
    return "Reboot player";
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
    BotAudioService botAudioService = new BotAudioService(context.getGuild());

    botAudioService.rebootPlayer();

    context.sendText("Reboot...");
//    event.reply("Reboot...").queue();

    log.warn("User reboot bot. " + "User: " + context.getUser());
  }
}