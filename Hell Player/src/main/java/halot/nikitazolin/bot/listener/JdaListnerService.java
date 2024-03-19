package halot.nikitazolin.bot.listener;

import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.command.manager.CommandEventHandler;
import halot.nikitazolin.bot.jda.JdaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class JdaListnerService {

  private final JdaService jdaService;
  private final CommandEventHandler commandEventHandler;

  public void addListners() {
    Optional<JDA> jda = jdaService.getJda();

    registerListners(jda);
  }

  private void registerListners(Optional<JDA> jda) {
    jda.ifPresentOrElse(jdaL -> {
      jdaL.addEventListener(commandEventHandler);
//      jdaL.addEventListener(another listener);
    }, () -> System.out.println("JDA is not present!"));

    log.info("registerListner: " + commandEventHandler);
  }
}
