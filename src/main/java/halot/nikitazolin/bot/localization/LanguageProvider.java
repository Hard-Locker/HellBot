package halot.nikitazolin.bot.localization;

public interface LanguageProvider {

  String getText(String key, Object... args);
}
