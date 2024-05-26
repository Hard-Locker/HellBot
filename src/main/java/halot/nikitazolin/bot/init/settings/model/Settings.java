package halot.nikitazolin.bot.init.settings.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Scope("singleton")
@Data
public class Settings {

  private String language = "en";
  private int volume = 100;
  private Long ownerUserId = null;
  private Long aloneTimeUntilStop = 120L;
  private String botStatus = "online";
  private boolean songInStatus = true;
  private boolean songInTopic = false;
  private boolean stayInChannel = true;
  private boolean updateAlerts = true;
  private List<Long> allowedTextChannelIds = new ArrayList<>();
  private List<Long> allowedVoiceChannelIds = new ArrayList<>();
  private List<Long> adminUserIds = new ArrayList<>();
  private List<Long> djUserIds = new ArrayList<>();
  private List<Long> bannedUserIds = new ArrayList<>();
  private List<String> playlistFolderPaths = new ArrayList<>();
  private List<String> prefixes = new ArrayList<>();
  private Map<String, List<String>> nameAliases = new HashMap<>();
}
