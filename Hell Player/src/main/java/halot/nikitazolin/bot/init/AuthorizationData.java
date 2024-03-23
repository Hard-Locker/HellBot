package halot.nikitazolin.bot.init;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.ToString;

@Component
@Scope("singleton")
@Getter
@ToString
public class AuthorizationData {

  private String apiKey;
  private boolean youtubeAuthorization;
  private String youtubeLogin;
  private String youtubePassword;
  private boolean databaseUse;
  private String dbName;
  private String dbUrl;
  private String dbUsername;
  private String dbPassword;

}
