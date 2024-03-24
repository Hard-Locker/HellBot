package halot.nikitazolin.bot.init.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Component
@Scope("singleton")
@Getter
@ToString
@EqualsAndHashCode
public class Config {
  
  private static String prefix = "1";
  private static boolean songInStatus = true;
  private static boolean stayInChannel = true;
  private static int aloneTimeUntilStop = 3000;
  private static boolean updateAlerts = false;
  
  
}
