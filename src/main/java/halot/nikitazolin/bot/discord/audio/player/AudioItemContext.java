package halot.nikitazolin.bot.discord.audio.player;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.command.BotCommandContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@Scope("prototype")
@Getter
@RequiredArgsConstructor
public class AudioItemContext {

  private final String url;
  private final BotCommandContext context;
}
