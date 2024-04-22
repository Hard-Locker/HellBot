package halot.nikitazolin.bot.init.settings.model;

import java.lang.reflect.Field;
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
  private Long ownerIds;
  private Long aloneTimeUntilStop;
  private String botStatusAtStart;
  private String botActivityAtStart;
  private boolean songInStatus;
  private boolean stayInChannel;
  private boolean updateAlerts;
  private List<Long> allowedTextChannelIds;
  private List<Long> allowedVoiceChannelIds;
  private List<Long> djUserIds;
  private List<Long> bannedUserIds;
  private List<String> playlistFolderPaths;
  private List<String> prefixes;
  private HashMap<String, List<String>> nameAliases;

//  public void printFieldValues() {
//    Field[] fields = this.getClass().getDeclaredFields(); // Get all fields from the class
//    for (Field field : fields) {
//      field.setAccessible(true); // Make field accessible if it is private
//      try {
//        Object value = field.get(this); // Get value of the field for this instance
//        System.out.println(field.getName() + ": " + (value != null ? value.toString() : "null"));
//      } catch (IllegalAccessException e) {
//        System.out.println("Error accessing field: " + field.getName());
//      }
//    }
//  }
}
