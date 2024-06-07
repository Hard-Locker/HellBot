package halot.nikitazolin.bot.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.HellBot;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Component
@Slf4j
public class VersionChecker {

  private static final OkHttpClient client = new OkHttpClient();

  public String getNumberCurrentVersion() {
    Package packag = HellBot.class.getPackage();

    if (packag != null) {
      String version = packag.getImplementationVersion();

      if (version != null) {
        return version;
      }
    }

    return "unknown";
  }

  public Optional<String> getNumberLatestVersion() {
    Request request = new Request.Builder().url("https://api.github.com/repos/Hard-Locker/HellBot/releases/latest")
        .get().build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        log.error("Failed to fetch latest version: HTTP {}", response.code());
        return Optional.empty();
      }

      try (ResponseBody body = response.body()) {
        if (body != null) {
          try (Reader reader = body.charStream()) {
            JSONObject obj = new JSONObject(new JSONTokener(reader));
            return Optional.ofNullable(obj.getString("name"));
          }
        } else {
          log.error("Response body is null");
          return Optional.empty();
        }
      }
    } catch (IOException | JSONException ex) {
      log.error("Exception occurred while fetching latest version", ex);
      return Optional.empty();
    }
  }
}
