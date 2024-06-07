package halot.nikitazolin.bot.discord.listener;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

@Component
@Slf4j
public class ReadyListener implements EventListener {

  @Override
  public void onEvent(@NotNull GenericEvent genericEvent) {
    if (genericEvent instanceof ReadyEvent) {
      log.info("JDA ready!");
      System.out.println("JDA ready!");
    }
  }
}
