package halot.nikitazolin.bot.init;

import org.springframework.stereotype.Component;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Component
@Getter
@ToString
@EqualsAndHashCode
//@RequiredArgsConstructor
public class BotConfig {
  
  private static String prefix = "1";
  private static boolean songInStatus = true;
  private static boolean stayInChannel = true;
  private static int aloneTimeUntilStop = 3000;
  private static boolean updateAlerts = false;
  
  
}
