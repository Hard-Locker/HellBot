package halot.nikitazolin.bot.discord.audio.player;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueueFiller {

  private final PlayerService playerService;

  public void fillQueue(List<String> identifiers) {
    for (String identifier : identifiers) {
      boolean result = playerService.getQueue().offer(identifier);

      if (result) {
        log.info("Add link {} to queue", identifier);
      } else {
        log.warn("Problem with adding link {} to queue", identifier);
      }
    }
  }
}
