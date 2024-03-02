package halot.nikitazolin.bot.audio;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.typesafe.config.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransformativeAudioSourceManager extends YoutubeAudioSourceManager {

  private final String name;
  private final String regex;
  private final String replacement;
  private final String selector;
  private final String format;

  public TransformativeAudioSourceManager(String name, Config object) {
    this(name, object.getString("regex"), object.getString("replacement"), object.getString("selector"),
        object.getString("format"));
  }

  @Override
  public String getSourceName() {
    return name;
  }

  @Override
  public AudioItem loadItem(AudioPlayerManager apm, AudioReference ar) {
    if (ar.identifier == null || !ar.identifier.matches(regex))
      return null;
    try {
      String url = ar.identifier.replaceAll(regex, replacement);
      Document doc = Jsoup.connect(url).get();
      String value = doc.selectFirst(selector).ownText();
      String formattedValue = String.format(format, value);
      
      return super.loadItem(apm, new AudioReference(formattedValue, null));
    } catch (PatternSyntaxException ex) {
      log.info(String.format("Invalid pattern syntax '%s' in source '%s'", regex, name));
    } catch (IOException ex) {
      log.warn(String.format("Failed to resolve URL in source '%s': ", name), ex);
    } catch (Exception ex) {
      log.warn(String.format("Exception in source '%s'", name), ex);
    }
    return null;
  }

  public static List<TransformativeAudioSourceManager> createTransforms(Config transforms) {
    try {
      return transforms.root().entrySet().stream()
          .map(e -> new TransformativeAudioSourceManager(e.getKey(), transforms.getConfig(e.getKey())))
          .collect(Collectors.toList());
    } catch (Exception ex) {
      log.warn("Invalid transform ", ex);
      return Collections.emptyList();
    }
  }
}
