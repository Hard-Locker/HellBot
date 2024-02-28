package halot.nikitazolin.bot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

@Component
@Data
@RequiredArgsConstructor
public class BotSetting {
//
//  private final Prompt prompt;
//  private Path path = null;
//  private OnlineStatus status;
//  private Activity game;
//  private Config aliases;
//  private Config transforms;
//  private static final String CONTEXT = "Config";
//  private static final String START_TOKEN = "/// START OF JMUSICBOT CONFIG ///";
//  private static final String END_TOKEN = "/// END OF JMUSICBOT CONFIG ///";
//  private String token;
//  private String prefix;
//  private String altprefix;
//  private String helpWord;
//  private String playlistsFolder;
//  private String successEmoji;
//  private String warningEmoji;
//  private String errorEmoji;
//  private String loadingEmoji;
//  private String searchingEmoji;
//  private boolean stayInChannel;
//  private boolean songInGame;
//  private boolean npImages;
//  private boolean updatealerts;
//  private boolean useEval;
//  private boolean dbots;
//  private boolean valid = false;
//  private long owner;
//  private long maxSeconds;
//  private long aloneTimeUntilStop;
//
//  public void load() {
//    valid = false;
//
//    try {
//      path = getConfigPath();
//      Config config = ConfigFactory.load();
//
//      token = config.getString("token");
//      prefix = config.getString("prefix");
//      altprefix = config.getString("altprefix");
//      helpWord = config.getString("help");
//      owner = config.getLong("owner");
//      successEmoji = config.getString("success");
//      warningEmoji = config.getString("warning");
//      errorEmoji = config.getString("error");
//      loadingEmoji = config.getString("loading");
//      searchingEmoji = config.getString("searching");
//      game = OtherUtil.parseGame(config.getString("game"));
//      status = OtherUtil.parseStatus(config.getString("status"));
//      stayInChannel = config.getBoolean("stayinchannel");
//      songInGame = config.getBoolean("songinstatus");
//      npImages = config.getBoolean("npimages");
//      updatealerts = config.getBoolean("updatealerts");
//      useEval = config.getBoolean("eval");
//      maxSeconds = config.getLong("maxtime");
//      aloneTimeUntilStop = config.getLong("alonetimeuntilstop");
//      playlistsFolder = config.getString("playlistsfolder");
//      aliases = config.getConfig("aliases");
//      transforms = config.getConfig("transforms");
//      dbots = owner == 113156185389092864L;
//
//      // we may need to write a new config file
//      boolean write = false;
//
//      // validate bot token
//      if (token == null || token.isEmpty() || token.equalsIgnoreCase("BOT_TOKEN_HERE")) {
//        token = prompt.prompt("Please provide a bot token." + "\nInstructions for obtaining a token can be found here:"
//            + "\nhttps://github.com/jagrosh/MusicBot/wiki/Getting-a-Bot-Token." + "\nBot Token: ");
//        if (token == null) {
//          prompt.alert(Prompt.Level.WARNING, CONTEXT,
//              "No token provided! Exiting.\n\nConfig Location: " + path.toAbsolutePath().toString());
//          return;
//        } else {
//          write = true;
//        }
//      }
//
//      // validate bot owner
//      if (owner <= 0) {
//        try {
//          owner = Long.parseLong(prompt.prompt("Owner ID was missing, or the provided owner ID is not valid."
//              + "\nPlease provide the User ID of the bot's owner."
//              + "\nInstructions for obtaining your User ID can be found here:"
//              + "\nhttps://github.com/jagrosh/MusicBot/wiki/Finding-Your-User-ID" + "\nOwner User ID: "));
//        } catch (NumberFormatException | NullPointerException ex) {
//          owner = 0;
//        }
//        if (owner <= 0) {
//          prompt.alert(Prompt.Level.ERROR, CONTEXT,
//              "Invalid User ID! Exiting.\n\nConfig Location: " + path.toAbsolutePath().toString());
//          return;
//        } else {
//          write = true;
//        }
//      }
//
//      if (write)
//        writeToFile();
//
//      // if we get through the whole config, it's good to go
//      valid = true;
//    } catch (ConfigException ex) {
//      prompt.alert(Prompt.Level.ERROR, CONTEXT,
//          ex + ": " + ex.getMessage() + "\n\nConfig Location: " + path.toAbsolutePath().toString());
//    }
//  }
//
//  private void writeToFile() {
//    byte[] bytes = loadDefaultConfig().replace("BOT_TOKEN_HERE", token).replace("0 // OWNER ID", Long.toString(owner))
//        .trim().getBytes();
//    try {
//      Files.write(path, bytes);
//    } catch (IOException ex) {
//      prompt.alert(Prompt.Level.WARNING, CONTEXT, "Failed to write new config options to config.txt: " + ex
//          + "\nPlease make sure that the files are not on your desktop or some other restricted area.\n\nConfig Location: "
//          + path.toAbsolutePath().toString());
//    }
//  }
//
//  private static String loadDefaultConfig() {
//    String original = OtherUtil.loadResource(new JMusicBot(), "/reference.conf");
//    return original == null ? "token = BOT_TOKEN_HERE\r\nowner = 0 // OWNER ID"
//        : original.substring(original.indexOf(START_TOKEN) + START_TOKEN.length(), original.indexOf(END_TOKEN)).trim();
//  }
//
//  private static Path getConfigPath() {
//    Path path = OtherUtil.getPath(System.getProperty("config.file", System.getProperty("config", "config.txt")));
//    if (path.toFile().exists()) {
//      if (System.getProperty("config.file") == null)
//        System.setProperty("config.file", System.getProperty("config", path.toAbsolutePath().toString()));
//      ConfigFactory.invalidateCaches();
//    }
//    return path;
//  }
//
//  public static void writeDefaultConfig() {
//    Prompt prompt = new Prompt(null, null, true, true);
//    prompt.alert(Prompt.Level.INFO, "JMusicBot Config", "Generating default config file");
//    Path path = BotConfig.getConfigPath();
//    byte[] bytes = BotConfig.loadDefaultConfig().getBytes();
//    try {
//      prompt.alert(Prompt.Level.INFO, "JMusicBot Config",
//          "Writing default config file to " + path.toAbsolutePath().toString());
//      Files.write(path, bytes);
//    } catch (Exception ex) {
//      prompt.alert(Prompt.Level.ERROR, "JMusicBot Config",
//          "An error occurred writing the default config file: " + ex.getMessage());
//    }
//  }
//
//  public boolean isValid() {
//    return valid;
//  }
//
//  public String getConfigLocation() {
//    return path.toFile().getAbsolutePath();
//  }
//
//  public String getPrefix() {
//    return prefix;
//  }
//
//  public String getAltPrefix() {
//    return "NONE".equalsIgnoreCase(altprefix) ? null : altprefix;
//  }
//
//  public String getToken() {
//    return token;
//  }
//
//  public long getOwnerId() {
//    return owner;
//  }
//
//  public String getSuccess() {
//    return successEmoji;
//  }
//
//  public String getWarning() {
//    return warningEmoji;
//  }
//
//  public String getError() {
//    return errorEmoji;
//  }
//
//  public String getLoading() {
//    return loadingEmoji;
//  }
//
//  public String getSearching() {
//    return searchingEmoji;
//  }
//
//  public Activity getGame() {
//    return game;
//  }
//
//  public OnlineStatus getStatus() {
//    return status;
//  }
//
//  public String getHelp() {
//    return helpWord;
//  }
//
//  public boolean getStay() {
//    return stayInChannel;
//  }
//
//  public boolean getSongInStatus() {
//    return songInGame;
//  }
//
//  public String getPlaylistsFolder() {
//    return playlistsFolder;
//  }
//
//  public boolean getDBots() {
//    return dbots;
//  }
//
//  public boolean useUpdateAlerts() {
//    return updatealerts;
//  }
//
//  public boolean useEval() {
//    return useEval;
//  }
//
//  public boolean useNPImages() {
//    return npImages;
//  }
//
//  public long getMaxSeconds() {
//    return maxSeconds;
//  }
//
//  public String getMaxTime() {
//    return FormatUtil.formatTime(maxSeconds * 1000);
//  }
//
//  public long getAloneTimeUntilStop() {
//    return aloneTimeUntilStop;
//  }
//
//  public boolean isTooLong(AudioTrack track) {
//    if (maxSeconds <= 0)
//      return false;
//    return Math.round(track.getDuration() / 1000.0) > maxSeconds;
//  }
//
//  public String[] getAliases(String command) {
//    try {
//      return aliases.getStringList(command).toArray(new String[0]);
//    } catch (NullPointerException | ConfigException.Missing e) {
//      return new String[0];
//    }
//  }
//
//  public Config getTransforms() {
//    return transforms;
//  }
}
