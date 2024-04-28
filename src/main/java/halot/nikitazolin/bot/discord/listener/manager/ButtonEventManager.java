package halot.nikitazolin.bot.discord.listener.manager;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class ButtonEventManager {
  
  public void processingEvent(ButtonInteractionEvent buttonEvent) {
    String componentId = buttonEvent.getComponentId();
    Message originalMessage = buttonEvent.getMessage();

    switch (componentId) {
    case "yesButton":
      buttonEvent.reply("You clicked Yes!").setEphemeral(true).queue();
      originalMessage.delete().queue();
      log.info("Clicked yesButton");
      break;
    case "noButton":
      buttonEvent.reply("You clicked No!").setEphemeral(true).queue();
      originalMessage.delete().queue();
      log.info("Clicked noButton");
      break;
    default:
      buttonEvent.reply("Unknown button").setEphemeral(true).queue();
      log.info("Clicked unknown button");
      break;
    }
  }
}
