package halot.nikitazolin.bot.init.authorization;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.authorization.manager.AuthorizationConsoleMenu;
import halot.nikitazolin.bot.init.authorization.manager.AuthorizationFileChecker;
import halot.nikitazolin.bot.init.authorization.manager.AuthorizationLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService {

  private final AuthorizationFileChecker authorizationFileChecker;
  private final AuthorizationConsoleMenu authorizationConsoleMenu;
  private final AuthorizationLoader authorizationLoader;

  public void validateAuthorization(String filePath) {
    boolean authorizationExists = authorizationFileChecker.ensureFileExists(filePath);

    if (authorizationExists == false) {
      log.warn("Authorization data not exist");
      authorizationConsoleMenu.showStartMenu(filePath);
    }

    authorizationLoader.load(filePath);
  }
}
