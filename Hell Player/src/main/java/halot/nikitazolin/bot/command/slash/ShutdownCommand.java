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
public class ShutdownCommand extends BotCommand {

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
  public void execute(BotCommandContext context) {
    SlashCommandInteractionEvent event = context.getSlashCommandEvent();
    Guild guild = event.getGuild();
    User user = event.getMember().getUser();
    BotAudioService botAudioService = new BotAudioService(guild);

    botAudioService.shutdown();

    event.reply("Shutdown...").queue();

    log.warn("User shutdowning bot. " + "User: " + user);

    System.exit(0);
  }
}