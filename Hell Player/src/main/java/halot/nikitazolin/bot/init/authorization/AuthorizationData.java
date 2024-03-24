package halot.nikitazolin.bot.init.authorization;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@Scope("singleton")
@Data
@RequiredArgsConstructor
public class AuthorizationData {

  private DiscordApi discordApi = new DiscordApi();
  private Youtube youtube = new Youtube();
  private Database database = new Database();

  @Data
  @RequiredArgsConstructor
  public class DiscordApi {
    private String apiKey;
  }

  @Data
  @RequiredArgsConstructor
  public class Youtube {
    private boolean youtubeEnabled;
    private String youtubeLogin;
    private String youtubePassword;
  }

  @Data
  @RequiredArgsConstructor
  public class Database {
    private boolean dbEnabled;
    private String dbName;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
  }
}
