package halot.nikitazolin.bot.init;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
@Scope("singleton")
@Data
@RequiredArgsConstructor
public class AuthorizationData {

  private String apiKey;
  private YoutubeAuthorization youtubeAuthorization = new YoutubeAuthorization();
  private DatabaseUse databaseUse = new DatabaseUse();

  @Data
  @RequiredArgsConstructor
  public static class YoutubeAuthorization {
    private boolean enabled;
    private String login;
    private String password;
  }

  @Data
  @RequiredArgsConstructor
  public static class DatabaseUse {
    private boolean enabled;
    private String dbName;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
  }
}
