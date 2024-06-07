package halot.nikitazolin.bot.discord.listener;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.listener.manager.ButtonEventManager;
import halot.nikitazolin.bot.discord.listener.manager.GenericGuildVoiceEventManager;
import halot.nikitazolin.bot.discord.listener.manager.MessageReceivedEventManager;
import halot.nikitazolin.bot.discord.listener.manager.ModalInteractionEventManager;
import halot.nikitazolin.bot.discord.listener.manager.SlashCommandInteractionEventManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventsListener extends ListenerAdapter {

  private final SlashCommandInteractionEventManager slashCommandInteractionEventManager;
  private final MessageReceivedEventManager messageReceivedEventManager;
  private final ButtonEventManager buttonEventManager;
  private final ModalInteractionEventManager modalInteractionEventManager;
  private final GenericGuildVoiceEventManager genericGuildVoiceEventManager;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent slashCommandInteractionEvent) {
    log.debug("Call event: {}", slashCommandInteractionEvent);
    slashCommandInteractionEventManager.processingEvent(slashCommandInteractionEvent);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {
    log.debug("Call event: {}", messageReceivedEvent);
    messageReceivedEventManager.processingEvent(messageReceivedEvent);
  }

  @Override
  public void onButtonInteraction(ButtonInteractionEvent buttonInteractionEvent) {
    log.debug("Call event: {}", buttonInteractionEvent);
    buttonEventManager.processingEvent(buttonInteractionEvent);
  }

  @Override
  public void onModalInteraction(ModalInteractionEvent modalInteractionEvent) {
    log.debug("Call event: {}", modalInteractionEvent);
    modalInteractionEventManager.processingEvent(modalInteractionEvent);
  }

  @Override
  public void onGenericGuildVoice(GenericGuildVoiceEvent genericGuildVoiceEvent) {
    log.debug("Call event: {}", genericGuildVoiceEvent);
    genericGuildVoiceEventManager.processingEvent(genericGuildVoiceEvent);
  }
}
