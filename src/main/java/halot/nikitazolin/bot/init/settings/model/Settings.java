package halot.nikitazolin.bot.init.settings.model;

import java.util.HashMap;
import java.util.List;

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
  private List<Long> allowedTextChannelIds;
  private List<Long> allowedVoiceChannelIds;
  private List<Long> adminUserIds;
  private List<Long> djUserIds;
  private List<Long> bannedUserIds;
  private List<String> playlistFolderPaths;
  private List<String> prefixes;
  private HashMap<String, List<String>> nameAliases;
}
