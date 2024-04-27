package halot.nikitazolin.bot.discord.audio.player;

import halot.nikitazolin.bot.discord.command.BotCommandContext;

public record AudioItemContext (String url, BotCommandContext context) {

}
