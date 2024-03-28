package halot.nikitazolin.bot.init.authorization.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Youtube {

  private boolean youtubeEnabled;
  private String youtubeLogin;
  private String youtubePassword;
}
