package halot.nikitazolin.bot.localization.action.command.music;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.localization.LanguageProvider;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MusicProvider implements LanguageProvider {

  private static final String VOCABULARY_PATH = "localization.halot.nikitazolin.bot.discord.action.command.music.music";
  private ResourceBundle resourceBundle;

  public void initializeLanguage(String language) {
    log.debug("Initialize localization {}, for {}", language, VOCABULARY_PATH);
    Locale locale = Locale.forLanguageTag(language);
    resourceBundle = ResourceBundle.getBundle(VOCABULARY_PATH, locale);
  }

  @Override
  public String getText(String key, Object... args) {
    return String.format(resourceBundle.getString(key), args);
  }
}
