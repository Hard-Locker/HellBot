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
  private final EventsListener eventsListener;

  public void addListeners() {
    Optional<JDA> jdaOptional = jdaMaker.getJda();

    jdaOptional.ifPresentOrElse(this::registerListeners, () -> {
      System.err.println("JDA is not present!");
      log.error("JDA is not present");
    });
  }

  private void registerListeners(JDA jda) {
    jda.addEventListener(eventsListener);
    log.info("Registered event listener: {}", eventsListener);
  }
}
