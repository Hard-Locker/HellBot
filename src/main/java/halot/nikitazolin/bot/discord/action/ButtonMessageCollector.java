package halot.nikitazolin.bot.discord.action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.action.model.ButtonMessage;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("singleton")
@Slf4j
public class ButtonMessageCollector {

  private Map<Long, ButtonMessage> buttonMessages = new ConcurrentHashMap<>();

  public void addButtonMessage(Long messageId, ButtonMessage buttonMessage) {
    buttonMessages.put(messageId, buttonMessage);

    log.debug("Collected button messages: {}, with message ID: {}", buttonMessage, messageId);
  }

  public ButtonMessage findButtonMessage(Long messageId) {
    return buttonMessages.getOrDefault(messageId, null);
  }
}
