package halot.nikitazolin.bot.discord.audio.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.ActionMessageCollector;
import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.tool.MessageFormatter;
import halot.nikitazolin.bot.discord.tool.MessageSender;
import halot.nikitazolin.bot.localization.action.command.music.MusicProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;

@Component
@Slf4j
@RequiredArgsConstructor
public class InputLinkLoader {

  private final GuildAudioService guildAudioService;
  private final ActionMessageCollector actionMessageCollector;
  private final MessageFormatter messageFormatter;
  private final MessageSender messageSender;
  private final MusicProvider musicProvider;
  private final YoutubeLinkManager youtubeLinkManager;
  private final LocalLinkManager localLinkManager;

  public boolean isPlaylist(String link) {
    if (youtubeLinkManager.isYouTubePlaylist(link)) {
      return true;
    }

    if (localLinkManager.isLocalDirectory(link) && localLinkManager.hasAudioFiles(link)) {
      return true;
    }

    return false;
  }

  public void loadAdditionalLink(Long messageId, String key) {
    ActionMessage actionMessage = actionMessageCollector.findMessage(messageId);

    if (actionMessage == null) {
      log.debug("Fail in getting ActionMessage with ID: {}", messageId);
      return;
    }

    Map<String, Object> additional = actionMessage.getAdditionalData();
    Object linksObject = additional.get(key);
    List<String> links = new ArrayList<>();

    if (linksObject instanceof List<?>) {
      List<?> rawList = (List<?>) linksObject;

      for (Object item : rawList) {
        if (item instanceof String) {
          links.add((String) item);
        }
      }

      List<String> extractedUrls = extractPlaylistLinks(links);
      guildAudioService.getPlayerService().fillQueue(extractedUrls, actionMessage.getContext());

      if (guildAudioService.getPlayerService().getAudioPlayer().getPlayingTrack() == null) {
        guildAudioService.getPlayerService().play();
      }

      EmbedBuilder embed = messageFormatter
          .createSuccessEmbed(extractedUrls.size() + " " + musicProvider.getText("play_command.message.adding_success")
              + ": " + actionMessage.getContext().getUser().getAsMention());
      messageSender.sendMessageEmbed(actionMessage.getContext().getTextChannel(), embed);
      log.debug("Loaded additional links");
    } else {
      EmbedBuilder embed = messageFormatter
          .createSuccessEmbed(musicProvider.getText("play_command.message.adding_error") + ": "
              + actionMessage.getContext().getUser().getAsMention());
      messageSender.sendMessageEmbed(actionMessage.getContext().getTextChannel(), embed);
      log.debug("Problem with downloading additional tracks");
    }
  }

  public List<String> processingInputLinks(List<String> rawLinks) {
    List<String> preparedLinks = new ArrayList<>();

    for (String link : rawLinks) {
      if (youtubeLinkManager.isYouTubeUrl(link)) {
        String simpleLink = youtubeLinkManager.extractSimpleUrl(link);
        preparedLinks.add(simpleLink);
        log.debug("Exctracted simlpe URL: {} from link: {}", simpleLink, link);
      } else {
        preparedLinks.add(link);
        log.debug("Added link: {}", link);
      }
    }

    return preparedLinks;
  }

  private List<String> extractPlaylistLinks(List<String> rawLinks) {
    List<String> extractedLinks = new ArrayList<>();

    for (String link : rawLinks) {
      if (youtubeLinkManager.isYouTubePlaylist(link)) {
        extractedLinks.addAll(youtubeLinkManager.extractVideoLinks(link));
        extractedLinks.remove(0);
        log.debug("Link have playlist. Link: {}", link);
      }

      if (localLinkManager.isLocalDirectory(link)) {
        List<String> audioFiles = localLinkManager.listAudioFiles(link);
        extractedLinks.addAll(audioFiles);
        log.debug("Link is a local directory. Link: {}, audio files found: {}", link, audioFiles.size());
      }
    }

    return extractedLinks;
  }
}
