package halot.nikitazolin.bot.jda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.audio.BotPlayerManager;
import halot.nikitazolin.bot.audio.IPlayerManager;
import halot.nikitazolin.bot.command.commands.HelloCommand;
import halot.nikitazolin.bot.command.commands.PingCommand;
import halot.nikitazolin.bot.command.commands.RebootCommand;
import halot.nikitazolin.bot.command.commands.ShutdownCommand;
import halot.nikitazolin.bot.command.commands.music.PlayCommand;
import halot.nikitazolin.bot.command.commands.music.SkipCommand;
import halot.nikitazolin.bot.command.commands.music.StopCommand;
import halot.nikitazolin.bot.command.manager.CommandEventHandler;
import halot.nikitazolin.bot.command.manager.CommandRegistrator;
import halot.nikitazolin.bot.command.manager.CommandSaver;
import halot.nikitazolin.bot.command.model.BotCommand;
import halot.nikitazolin.bot.listener.ReadyListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Component
@Scope("singleton")
@Slf4j
public class JdaService {

  private String BOT_TOKEN;
  private String status = "на твою мамку";
  private final List<GatewayIntent> gatewayIntents = List.of(
      GatewayIntent.GUILD_MESSAGES,
      GatewayIntent.DIRECT_MESSAGES,
      GatewayIntent.MESSAGE_CONTENT,
      GatewayIntent.GUILD_MESSAGE_REACTIONS,
      GatewayIntent.DIRECT_MESSAGE_REACTIONS,
      GatewayIntent.GUILD_VOICE_STATES,
      GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
      GatewayIntent.SCHEDULED_EVENTS
      );
  private final List<CacheFlag> cacheFlags = List.of(
      CacheFlag.VOICE_STATE,
      CacheFlag.EMOJI,
      CacheFlag.STICKER,
      CacheFlag.SCHEDULED_EVENTS
      );
  private List<CommandData> commandsToRegistration = new ArrayList<>();
  
  private final IPlayerManager botPlayerManager;
  private final CommandSaver commandSaver;
  private final CommandRegistrator commandRegistrator;
  private JDA jda;
  
  public JdaService(IPlayerManager botPlayerManager, CommandSaver commandSaver, CommandRegistrator commandRegistrator) {
    this.botPlayerManager = botPlayerManager;
    this.commandSaver = commandSaver;
    this.commandRegistrator = commandRegistrator;
    
    //Make JDA
    readTokenFromFile();
    createJda(gatewayIntents, cacheFlags);
    
    //Prepare commands
    commandsToRegistration = preparateCommands();
    registerCommands(getJda());
  }

  private void createJda(List<GatewayIntent> gatewayIntents, List<CacheFlag> cacheFlags) {
    try {
      jda = JDABuilder.createDefault(BOT_TOKEN, gatewayIntents)
          .setActivity(Activity.watching(status))
          .enableIntents(gatewayIntents)
          .addEventListeners(new CommandEventHandler(commandSaver), new ReadyListener())
          .enableCache(cacheFlags)
          .build();

      jda.awaitReady();
    } catch (InterruptedException e) {
      log.error("Interrupt: ", e);
    }
  }
  
  private void readTokenFromFile() {
    try {
      BOT_TOKEN = new String(Files.readAllBytes(Paths.get("src/main/resources/bot-token.txt"))).trim();
    } catch (IOException e) {
      log.error("Error read token: ", e);
    }
  }
  
  private void registerCommands(Optional<JDA> jda) {
    jda.ifPresentOrElse(jdaL -> {
      jdaL.updateCommands()
        .addCommands(commandsToRegistration)
        .queue();
    }, () -> System.out.println("JDA is not present!"));
  }

  private List<CommandData> preparateCommands() {
    System.out.println("Call preparateCommands");
    List<CommandData> commands = new ArrayList<>();

    BotCommand helloCommand = new HelloCommand();
    BotCommand pingCommand = new PingCommand();
    BotCommand playCommand = new PlayCommand(botPlayerManager);
    BotCommand skipCommand = new SkipCommand();
    BotCommand stopCommand = new StopCommand();
    BotCommand shutdownCommand = new ShutdownCommand();
    BotCommand rebootCommand = new RebootCommand();

    commands.add(create(helloCommand));
    commandSaver.fillActiveCommand(helloCommand);

    commands.add(create(pingCommand));
    commandSaver.fillActiveCommand(pingCommand);

    commands.add(create(playCommand));
    commandSaver.fillActiveCommand(playCommand);

    commands.add(create(skipCommand));
    commandSaver.fillActiveCommand(skipCommand);

    commands.add(create(stopCommand));
    commandSaver.fillActiveCommand(stopCommand);

    commands.add(create(shutdownCommand));
    commandSaver.fillActiveCommand(shutdownCommand);

    commands.add(create(rebootCommand));
    commandSaver.fillActiveCommand(rebootCommand);

    return commands;
  }
  
  private CommandData create(BotCommand botCommand) {
    if (botCommand.options().length > 0) {
      log.info("Registering command " + botCommand.name() + " with " + botCommand.options().length + " options!");
      return Commands.slash(botCommand.name(), botCommand.description()).addOptions(botCommand.options()).setGuildOnly(botCommand.guildOnly());
    } else {
      log.warn("Registering command " + botCommand.name() + " with no options!");
      return Commands.slash(botCommand.name(), botCommand.description()).setGuildOnly(botCommand.guildOnly());
    }
  }

  public Optional<JDA> getJda() {
    return Optional.ofNullable(jda);
  }
}
