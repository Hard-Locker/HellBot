package halot.nikitazolin.bot.command.slash;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.audio.BotAudioService;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.command.model.BotCommandContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Slf4j
public class RebootCommand extends BotCommand {

  @Override
  public String name() {
    return "reboot";
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
    SlashCommandInteractionEvent event = context.getSlashCommandEvent();
    Guild guild = context.getGuild();
    User user = context.getUser();
    BotAudioService botAudioService = new BotAudioService(guild);

    botAudioService.rebootPlayer();

    event.reply("Reboot...").queue();

    log.warn("User reboot bot. " + "User: " + user);
  }
}