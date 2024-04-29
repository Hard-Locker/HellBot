package halot.nikitazolin.bot.discord.listener;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.listener.manager.ButtonEventManager;
import halot.nikitazolin.bot.discord.listener.manager.MessageReceivedEventManager;
import halot.nikitazolin.bot.discord.listener.manager.SlashCommandEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventListener extends ListenerAdapter {

  private final SlashCommandEventManager slashCommandEventManager;
  private final MessageReceivedEventManager messageReceivedEventManager;
  private final ButtonEventManager buttonEventManager;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent slashEvent) {
    slashCommandEventManager.processingEvent(slashEvent);
    log.debug("Call slashEvent: {}", slashEvent);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent messageEvent) {
    messageReceivedEventManager.processingEvent(messageEvent);
    log.debug("Call messageEvent: {}", messageEvent);
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent buttonEvent) {
    buttonEventManager.processingEvent(buttonEvent);
    log.debug("Call buttonEvent: {}", buttonEvent);
  }
}
