package halot.nikitazolin.bot.command.slash;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.audio.BotAudioService;
import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Slf4j
public class ShutdownCommand extends SlashCommand {

  @Override
  public String name() {
    return "shutdown";
  }

  @Override
  public String description() {
    return "Shutdown bot";
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
  public void execute(SlashCommandRecord info) {
    SlashCommandInteractionEvent event = info.slashCommandEvent();
    Guild guild = event.getGuild();
    User user = event.getMember().getUser();
    BotAudioService botAudioService = new BotAudioService(guild);
    
    botAudioService.stopAudioSending();
//    botAudioService.getAudioSendHandler().getAudioPlayer().destroy();
    botAudioService.getAudioSendHandler().getAudioPlayerManager().shutdown();
    
    event.reply("Shutdown...").queue();
    
    log.warn("User shutdowning bot. " + "User: " + user);
    
    System.exit(0);
  }
}