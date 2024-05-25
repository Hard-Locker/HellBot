package halot.nikitazolin.bot.discord.action.command.music;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import halot.nikitazolin.bot.discord.action.BotCommandContext;
import halot.nikitazolin.bot.discord.action.model.BotCommand;
import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.util.TimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class NowPlayingCommand extends BotCommand {

  private final PlayerService playerService;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final Settings settings;
  private final TimeConverter timeConverter;

  private final String commandName = "now";

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
    return "Show now playing audio";
  }

  @Override
  public boolean checkUserPermission(User user) {
    List<Long> allowedIds = new ArrayList<>();

    if (settings.getBannedUserIds() != null) {
      allowedIds.addAll(settings.getBannedUserIds());
    }

    if (!allowedIds.contains(user.getIdLong())) {
      return true;
    } else {
      return false;
    }
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
    if (checkUserPermission(context.getUser()) == false) {
      EmbedBuilder embed = messageFormatter.createAltInfoEmbed("You have not permission for use this command");
      messageSender.sendPrivateMessage(context.getUser(), embed);
      log.debug("User have not permission for show now playing audio" + context.getUser());

      return;
    }

    if (playerService.getAudioPlayer().getPlayingTrack() != null) {
      AudioTrack audioTrack = playerService.getAudioPlayer().getPlayingTrack();
      AudioTrackInfo audioTrackInfo = audioTrack.getInfo();

      EmbedBuilder embed = messageFormatter.createInfoEmbed("Now playing");
      embed.setThumbnail(audioTrackInfo.artworkUrl);
      embed.addField("Name", audioTrackInfo.title, false);
      embed.addField("Author", audioTrackInfo.author, false);
      embed.addField("URL", audioTrackInfo.uri, false);
      embed.addField("Duration", timeConverter.convertLongTimeToSimpleFormat(audioTrackInfo.length), false);

      messageSender.sendMessageEmbed(context.getTextChannel(), embed);
      log.debug("User show now playing audio. " + "User: " + context.getUser());
    }
  }

  @Override
  public void buttonClickProcessing(ButtonInteractionEvent buttonEvent) {
    return;
  }

  @Override
  public void modalInputProcessing(ModalInteractionEvent modalEvent) {
    return;
  }
}