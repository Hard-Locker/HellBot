package halot.nikitazolin.bot.init.authorization;

import org.springframework.stereotype.Service;

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
      authorizationConsoleMenu.showMenu(filePath);
    }

    authorizationLoader.load(filePath);
  }
}
