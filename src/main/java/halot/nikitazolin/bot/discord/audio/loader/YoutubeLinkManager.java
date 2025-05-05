package halot.nikitazolin.bot.discord.audio.loader;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class YoutubeLinkManager {

  private static final String PROTOCOL_REGEX = "?(http://|https://)";
  private static final String DOMAIN_REGEX = "?(www\\.|music\\.)(youtube\\.com|youtu\\.be)";
  private static final String BASE_URL_REGEX = PROTOCOL_REGEX + DOMAIN_REGEX;
  private static final String VIDEO_ID_REGEX = "?(/watch\\?v=|/)";

  private static final Pattern YOUTUBE_URL = Pattern.compile("^" + BASE_URL_REGEX + "/.*", Pattern.CASE_INSENSITIVE);
  private static final Pattern YOUTUBE_URL_WITH_PLAYLIST = Pattern.compile("^" + BASE_URL_REGEX + "/.*[?&]list=.*$",
      Pattern.CASE_INSENSITIVE);
  private static final Pattern DIRECT_VIDEO_URL = Pattern.compile("^" + BASE_URL_REGEX + VIDEO_ID_REGEX + "[^&?]*",
      Pattern.CASE_INSENSITIVE);

  // TODO Need improve processing incorrect playlist URL. Incorrect URL have not
  // "www."
  public List<String> extractVideoLinks(String playlistUrl) {
    WebDriverManager.chromedriver().driverVersion("136.0.7103.48").setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    WebDriver webDriver = new ChromeDriver(options);

    List<String> videoLinks = new ArrayList<>();

    try {
      webDriver.get(playlistUrl);
      webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5L));

//      String selector = "ytd-playlist-panel-renderer a#wc-endpoint";
      String selector = "ytd-playlist-panel-video-renderer a#wc-endpoint";
      List<WebElement> videoElements = webDriver.findElements(By.cssSelector(selector));
      log.debug("Found {} video elements", videoElements.size());

      for (WebElement videoElement : videoElements) {
        String videoUrl = videoElement.getAttribute("href");
        String extractedUrl = extractSimpleUrl(videoUrl);
        videoLinks.add(extractedUrl);
      }
    } finally {
      webDriver.quit();
      log.debug("WebDriver has been closed");
    }

    return videoLinks;
  }

  public String extractSimpleUrl(String url) {
    log.debug("Extracting simple URL from: {}", url);
    Matcher matcher = DIRECT_VIDEO_URL.matcher(url);

    if (matcher.find()) {
      String extractedUrl = matcher.group();
      log.debug("Extracted URL: {}", extractedUrl);

      return extractedUrl;
    }

    log.debug("No match found for URL: {}", url);

    return url;
  }

  public boolean isYouTubeUrl(String url) {
    log.debug("Checking if URL is a YouTube URL: {}", url);
    boolean matches = YOUTUBE_URL.matcher(url).matches();
    log.debug("URL {} is a YouTube URL: {}", url, matches);

    return matches;
  }

  public boolean isYouTubePlaylist(String url) {
    log.debug("Checking if URL is a YouTube playlist: {}", url);
    boolean matches = YOUTUBE_URL_WITH_PLAYLIST.matcher(url).matches();
    log.info("URL {} is a YouTube playlist: {}", url, matches);

    return matches;
  }
}
