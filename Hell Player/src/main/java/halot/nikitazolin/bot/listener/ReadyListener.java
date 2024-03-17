package halot.nikitazolin.bot.listener;

import org.jetbrains.annotations.NotNull;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

@Slf4j
public class ReadyListener implements EventListener {

  @Override
  public void onEvent(@NotNull GenericEvent genericEvent) {
    if (genericEvent instanceof ReadyEvent) {
      log.info("Bot ready!");
    }
  }
}
