package halot.nikitazolin.bot.repository;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.authorization.data.AuthorizationData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DbService {
  
  private final AuthorizationData authorizationData;
  
  public void connectDb() {
    
  }

  private void checkDb() {
    if (authorizationData.getDatabase().isDbEnabled() == true) {
      //
    }
  }
  
}
