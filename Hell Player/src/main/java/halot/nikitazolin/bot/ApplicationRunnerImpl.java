package halot.nikitazolin.bot;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Profile("development")
@RequiredArgsConstructor
public class ApplicationRunnerImpl implements ApplicationRunner {

  private static JdaService jdaService;
//private static CommandRegistrator commandRegistry;
  
  @Override
  public void run(ApplicationArguments args) throws Exception {
    jdaService = new JdaService();
//  commandRegistry = new CommandRegistrator();

  }

  public static JdaService getJdaService() {
    return jdaService;
  }

//public static CommandRegistrator getCommandRegistry() {
//  return commandRegistry;
//}
}
