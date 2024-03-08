package halot.nikitazolin.bot.command.slash.music;

import java.util.List;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.audio.BotAudioService;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Slf4j
public class StopCommand extends BotCommand {

  @Override
  public String name() {
    return "stop";
  }
  
  @Override
  public List<String> nameAliases() {
    return List.of("stop");
  }

  @Override
  public String description() {
    return "Stop playing music";
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
    Guild guild = context.getGuild();
    User user = context.getUser();
    BotAudioService botAudioService = new BotAudioService(guild);

    botAudioService.stopAudioSending();
//    context.getSlashCommandEvent().reply("Player was stopped by user: " + user.getAsMention()).queue();
    
    log.info("Player was stopped by user: " + user);
  }
}