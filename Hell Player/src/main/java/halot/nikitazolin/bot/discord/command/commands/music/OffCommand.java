package halot.nikitazolin.bot.discord.command.commands.music;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.command.model.BotCommand;
import halot.nikitazolin.bot.discord.command.model.BotCommandContext;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class OffCommand extends BotCommand {

  private final GuildAudioService guildAudioService;
  private final MessageUtil messageUtil;
  
  @Override
  public String name() {
    return "off";
  }

  @Override
  public List<String> nameAliases() {
    return List.of("off", "999");
  }

  @Override
  public List<String> commandPrefixes() {
    return List.of("!", "1");
  }

  @Override
  public String description() {
    return "Stop music, clear playlist, and disconnect from voice channel";
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
    guildAudioService.stopAudioSending();

    EmbedBuilder embed = messageUtil.createWarningEmbed("Player was go off by user: " + context.getUser().getAsMention());
    context.sendMessageEmbed(embed);

    log.info("Player was go off by user: " + context.getUser());
  }
}