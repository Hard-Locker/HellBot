package halot.nikitazolin.bot.discord.action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.model.ActionMessage;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("singleton")
@Slf4j
public class ActionMessageCollector {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private Map<Long, ActionMessage> actionMessages = new ConcurrentHashMap<>();

  public void addMessage(Long messageId, ActionMessage actionMessage) {
    actionMessages.put(messageId, actionMessage);
    log.debug("Collected messages: {}, with message ID: {}", actionMessage, messageId);

    scheduler.schedule(() -> {
      actionMessages.remove(messageId);
      log.debug("Message is no longer available for processing. Message ID: {}", messageId);
    }, actionMessage.getExpireTime(), TimeUnit.MILLISECONDS);
  }

  public ActionMessage findMessage(Long messageId) {
    return actionMessages.getOrDefault(messageId, null);
  }
}
