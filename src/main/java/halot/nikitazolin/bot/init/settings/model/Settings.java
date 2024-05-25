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

  private int volume;
  private Long ownerUserId;
  private Long aloneTimeUntilStop;
  private String botStatus;
  private String botActivity;
  private boolean songInStatus;
  private boolean stayInChannel;
  private boolean updateAlerts;
  private List<Long> allowedTextChannelIds = new ArrayList<>();
  private List<Long> allowedVoiceChannelIds = new ArrayList<>();
  private List<Long> adminUserIds = new ArrayList<>();
  private List<Long> djUserIds = new ArrayList<>();
  private List<Long> bannedUserIds = new ArrayList<>();
  private List<String> playlistFolderPaths = new ArrayList<>();
  private List<String> prefixes = new ArrayList<>();
  private Map<String, List<String>> nameAliases = new HashMap<>();
}
