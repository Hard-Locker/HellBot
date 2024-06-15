package halot.nikitazolin.bot.discord.audio.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LocalLinkManager {

  public boolean isLocalDirectory(String link) {
    Path path = Paths.get(link);
    return Files.exists(path);
  }

  public boolean hasAudioFiles(String path) {
    String directoryPath = extractDirectoryPathFromFile(path);

    try (Stream<Path> files = Files.list(Paths.get(directoryPath))) {
      log.debug("Checking for audio files in directory: {}", directoryPath);
      return files.filter(Files::isRegularFile).map(Path::toString).anyMatch(this::isAudioFile);
    } catch (IOException e) {
      log.error("Error reading directory: " + e.getMessage());
      return false;
    }
  }

  public List<String> listAudioFiles(String path) {
    String directoryPath = extractDirectoryPathFromFile(path);

    try (Stream<Path> files = Files.list(Paths.get(directoryPath))) {
      log.debug("Collect files in directory: {}", directoryPath);
      return files.filter(Files::isRegularFile).map(Path::toString).filter(this::isAudioFile).toList();
    } catch (IOException e) {
      log.error("Error reading directory: " + e.getMessage());
      return List.of();
    }
  }

  private String extractDirectoryPathFromFile(String link) {
    Path path = Paths.get(link);

    if (Files.isRegularFile(path)) {
      return path.getParent().toString();
    }

    return link;
  }

  private boolean isAudioFile(String file) {
    String lowerCaseFile = file.toLowerCase();
    return lowerCaseFile.endsWith(".flac") || lowerCaseFile.endsWith(".mp3") || lowerCaseFile.endsWith(".mp4")
        || lowerCaseFile.endsWith(".m4a") || lowerCaseFile.endsWith(".ogg") || lowerCaseFile.endsWith(".wav");
  }
}
