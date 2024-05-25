package halot.nikitazolin.bot.discord.tool;

import java.io.IOException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

@Component
@Slf4j
@RequiredArgsConstructor
public class ActivityManager {

  private final JdaMaker jdaMaker;

  public void setPlaying(String game) {
    setActivity(Activity.playing(game));
  }

  public void setWatching(String video) {
    setActivity(Activity.watching(video));
  }

  public void setListening(String song) {
    setActivity(Activity.listening(song));
  }

  public void setStreaming(String url) {
    String streamName = getStreamNameFromUrl(url);
    setActivity(Activity.streaming(streamName, url));
  }

  public void setCustomStatus(String custom) {
    setActivity(Activity.customStatus(custom));
  }

  private void setActivity(Activity activity) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();
      jda.getPresence().setActivity(activity);
      log.debug("Activity set to: " + activity.getName());
    } else {
      log.error("Failed to set activity: JDA instance not available");
    }
  }

  private String getStreamNameFromUrl(String url) {
    try {
      Document doc = Jsoup.connect(url).get();
      Element titleElement = doc.selectFirst("title");

      if (titleElement != null) {
        return titleElement.text();
      } else {
        log.debug("Title element not found in the page: " + url);
      }
    } catch (IOException e) {
      log.debug("Failed to fetch the stream name from URL: " + url, e);
    }

    return "Unknown Stream";
  }
}
