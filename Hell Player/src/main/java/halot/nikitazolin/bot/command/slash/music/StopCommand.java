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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
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
    System.out.println("Execute stop command");
    
    SlashCommandInteractionEvent slashEvent = context.getSlashCommandEvent();
//    MessageReceivedEvent messageEvent = context.getMessageReceivedEvent();
//    Guild guild = null;
//    
//    System.out.println(slashEvent.getGuild());
//    System.out.println(messageEvent.getGuild());
//    
//    if(slashEvent.getGuild() != null) {
//      guild = slashEvent.getGuild();
//    }else if(messageEvent.getGuild() != null) {
//      guild = messageEvent.getGuild();
//    }
    
    Guild guild = slashEvent.getGuild();
    User user = slashEvent.getMember().getUser();
    BotAudioService botAudioService = new BotAudioService(guild);

    botAudioService.stopAudioSending();
    slashEvent.reply("Player was stopped by user: " + user.getAsMention()).queue();

    log.info("Player was stopped by user: " + user);
  }
}