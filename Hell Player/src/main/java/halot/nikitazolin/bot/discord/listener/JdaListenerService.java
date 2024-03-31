package halot.nikitazolin.bot.discord.listener;

import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.command.manager.CommandEventHandler;
import halot.nikitazolin.bot.discord.jda.JdaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class JdaListenerService {

  private final JdaService jdaService;
  private final CommandEventHandler commandEventHandler;

  public void addListeners() {
    Optional<JDA> jda = jdaService.getJda();

    registerListeners(jda);
  }

  private void registerListeners(Optional<JDA> jda) {
    jda.ifPresentOrElse(jdaL -> {
      jdaL.addEventListener(commandEventHandler);
      log.info("register JDA Listener: " + commandEventHandler);
//      jdaL.addEventListener(another listener);
    }, () -> System.out.println("JDA is not present!"));

  }
}
