package halot.nikitazolin.bot.localization;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.localization.action.PermissionProvider;
import halot.nikitazolin.bot.localization.action.command.common.CommonProvider;
import halot.nikitazolin.bot.localization.action.command.music.MusicProvider;
import halot.nikitazolin.bot.localization.action.command.setting.SettingProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalizationService {

  private final Settings settings;
  private final PermissionProvider permissionProvider;
  private final SettingProvider settingProvider;
  private final CommonProvider commonProvider;
  private final MusicProvider musicProvider;
  
  public void initializeLocale() {
    log.debug("Start initialize localization");
    String language = settings.getLanguage();
    
    permissionProvider.initializeLanguage(language);
    settingProvider.initializeLanguage(language);
    commonProvider.initializeLanguage(language);
    musicProvider.initializeLanguage(language);
  }
}
