package halot.nikitazolin.bot.listener;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

@Component
public class ReadyListener implements EventListener {

  @Override
  public void onEvent(@NotNull GenericEvent genericEvent) {
    if (genericEvent instanceof ReadyEvent) {
      System.out.println("Bot ready!");
    }
  }
}
