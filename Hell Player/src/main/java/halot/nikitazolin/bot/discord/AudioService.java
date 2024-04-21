package halot.nikitazolin.bot.discord;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.audio.player.AudioPlayerListenerService;
import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

@Service
@Slf4j
@RequiredArgsConstructor
public class AudioService {

  private final PlayerService playerService;
  private final GuildAudioService guildAudioService;
  private final AudioPlayerListenerService audioPlayerListenerService;

  public void makeAudioPlayer(Guild guild) {
    playerService.createPlayer();
    audioPlayerListenerService.addListeners();
    guildAudioService.registratePlayer(guild);

    log.info("Make AudioPlayer for guild: " + guild);
  }
}
