package halot.nikitazolin.bot.discord.command.commands.music;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.audio.player.QueueFiller;
import halot.nikitazolin.bot.discord.command.model.BotCommand;
import halot.nikitazolin.bot.discord.command.model.BotCommandContext;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class PlayCommand extends BotCommand {

  private final GuildAudioService guildAudioService;
  private final MessageUtil messageUtil;
  private final QueueFiller queueFiller;
  private final Settings settings;

  @Override
  public String name() {
    return "play";
  }

  @Override
  public List<String> nameAliases() {
    return List.of("play", "1");
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
    return "Start playing music from link";
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
    return new OptionData[] { new OptionData(OptionType.STRING, "link", "URL with content", false) };
  }

  @Override
  public void execute(BotCommandContext context) {
    String url0 = "https://www.youtube.com/watch?v=Tctv-vujFKU";
    String url1 = "D:\\Music\\Folders\\2024\\Kidd Russell - Fade (Минус).mp3";
    String url2 = "D:\\Music\\Folders\\2023\\30 Seconds To Mars - Attack.mp3";
    String url3 = "https://youtu.be/Tctv-vujFKU?si=731Seio46tir0SgO";
    List<String> links = List.of(url0, url1, url2, url3);

//    List<String> links = context.getCommandArguments().getString();

    queueFiller.fillQueue(links);

    if (guildAudioService.connectToVoiceChannel(context) == false) {
      return;
    }

    guildAudioService.getPlayerService().play();

    if (!links.isEmpty()) {
      EmbedBuilder embed = messageUtil.createSuccessEmbed("Added audiotrack");
      context.sendMessageEmbed(embed);
    }

    log.debug("User added links to playing music. " + "User: " + context.getUser());
  }
}