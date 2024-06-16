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

  public List<String> extractVideoLinks(String playlistUrl) {
    WebDriverManager.chromedriver().setup();
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
    Pattern youtubePattern = Pattern.compile("^(https://)(www\\.youtube\\.com|youtu\\.be)/watch\\?v=[^&]*",
        Pattern.CASE_INSENSITIVE);
    Matcher matcher = youtubePattern.matcher(url);

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
    Pattern youtubePattern = Pattern.compile("^(https://)(www\\.youtube\\.com|youtu\\.be)/.*",
        Pattern.CASE_INSENSITIVE);

    boolean matches = youtubePattern.matcher(url).matches();
    log.debug("URL {} is a YouTube URL: {}", url, matches);
    return matches;
  }

  public boolean isYouTubePlaylist(String url) {
    log.debug("Checking if URL is a YouTube playlist: {}", url);
    Pattern youtubePattern = Pattern.compile("^(https://)(www\\.youtube\\.com|youtu\\.be)/.*[?&]list=.*$",
        Pattern.CASE_INSENSITIVE);

    boolean matches = youtubePattern.matcher(url).matches();
    log.info("URL {} is a YouTube playlist: {}", url, matches);
    return matches;
  }
}
