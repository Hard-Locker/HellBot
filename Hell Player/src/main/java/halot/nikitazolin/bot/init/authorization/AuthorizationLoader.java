package halot.nikitazolin.bot.init.authorization;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationLoader {

  private final AuthorizationData authorizationData;
  
  public void load(String filePath) {
    
  }
}
