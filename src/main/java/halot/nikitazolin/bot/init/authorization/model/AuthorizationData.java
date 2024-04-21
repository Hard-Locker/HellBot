package halot.nikitazolin.bot.init.authorization.model;

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
}
