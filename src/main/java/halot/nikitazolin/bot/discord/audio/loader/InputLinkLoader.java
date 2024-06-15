package halot.nikitazolin.bot.discord.audio.loader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
import net.dv8tion.jda.api.entities.Message.Attachment;

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

    if (rawLinks.isEmpty() == true) {
      return new ArrayList<>();
    }

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

//  public List<String> processingInputAttachments(List<Attachment> attachments) {
//    List<String> innerLinks = new ArrayList<>();
//
//    if (attachments.isEmpty()) {
//      return innerLinks;
//    }
//
//    List<CompletableFuture<Void>> futures = attachments.stream().filter(this::isAudioAttachment)
//        .map(attachment -> processAttachment(attachment, innerLinks)).toList();
//
//    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//
//    return innerLinks;
//  }

  public List<String> processingInputAttachments(List<Attachment> attachments) {
    List<Path> filePaths = new ArrayList<>();

    if (attachments.isEmpty()) {
      return new ArrayList<>();
    }

    List<CompletableFuture<Path>> futures = attachments.stream().filter(this::isAudioAttachment)
        .map(this::processAttachment).toList();

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    for (CompletableFuture<Path> future : futures) {
      try {
        Path path = future.get();

        if (path != null) {
          filePaths.add(path);
        }
      } catch (Exception e) {
        log.error("Failed to get future result", e);
      }
    }

    return filePaths.stream().map(Path::toString).toList();
  }

  private boolean isAudioAttachment(Attachment attachment) {
    String extension = "." + attachment.getFileExtension();
    return localLinkManager.isAudioFile(extension);
  }

//  private CompletableFuture<Void> processAttachment(Attachment attachment, List<String> innerLinks) {
//    try {
//      File tempDir = new File("src/main/resources/temp");
//
//      if (!tempDir.exists()) {
//        tempDir.mkdirs();
//      }
//
//      String extension = "." + attachment.getFileExtension();
//      Path tempFile = Files.createTempFile(tempDir.toPath(), "audio_", extension);
//
//      return attachment.getProxy().downloadToFile(tempFile.toFile()).thenAccept(file -> {
//        log.debug("File downloaded: " + file.getPath());
//        innerLinks.add(file.getPath());
//        file.deleteOnExit();
//      }).exceptionally(e -> {
//        log.error("Failed to download file", e);
//        return null;
//      });
//    } catch (IOException e) {
//      log.error("Failed to create temp file", e);
//      return CompletableFuture.completedFuture(null);
//    }
//  }

  private CompletableFuture<Path> processAttachment(Attachment attachment) {
    try {
      File tempDir = new File("src/main/resources/temp");

      if (!tempDir.exists()) {
        tempDir.mkdirs();
      }

      String extension = "." + attachment.getFileExtension();
      Path tempFile = Files.createTempFile(tempDir.toPath(), "audio_", extension);

      return attachment.getProxy().downloadToFile(tempFile.toFile()).thenApply(file -> {
        log.debug("File downloaded: " + file.getPath());
        file.deleteOnExit();
        return file.toPath();
      }).exceptionally(e -> {
        log.error("Failed to download file", e);
        return null;
      });
    } catch (IOException e) {
      log.error("Failed to create temp file", e);
      return CompletableFuture.completedFuture(null);
    }
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
