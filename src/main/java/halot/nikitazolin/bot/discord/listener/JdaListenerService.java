package halot.nikitazolin.bot.discord.listener;

import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class JdaListenerService {

  private final JdaMaker jdaMaker;
  private final EventHandler eventHandler;

  public void addListeners() {
    Optional<JDA> jda = jdaMaker.getJda();

    registerListeners(jda);
  }

  private void registerListeners(Optional<JDA> jda) {
    jda.ifPresentOrElse(jdaL -> {
      jdaL.addEventListener(eventHandler);
      log.info("register event listener: " + eventHandler);
    }, () -> System.out.println("JDA is not present!"));
  }
}
