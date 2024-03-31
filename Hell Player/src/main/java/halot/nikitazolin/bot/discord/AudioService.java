package halot.nikitazolin.bot.discord;

import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.audio.GuildAudioService;
import halot.nikitazolin.bot.discord.audio.player.AudioPlayerListenerService;
import halot.nikitazolin.bot.discord.audio.player.IPlayerService;
import halot.nikitazolin.bot.discord.audio.player.TrackScheduler;
import halot.nikitazolin.bot.discord.jda.JdaMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;

@Service
@Slf4j
@RequiredArgsConstructor
public class AudioService {

  private final JdaMaker jdaMaker;
  private final IPlayerService playerService;
  private final GuildAudioService guildAudioService;
  private final TrackScheduler trackScheduler;
  private final AudioPlayerListenerService audioPlayerListenerService;

  public void makeAudioPlayer() {
    // TODO Need improve guild getter. Now it potential bug
    Guild guild = jdaMaker.getJda().get().getGuilds().getFirst();
    playerService.createPlayer();
    trackScheduler.preparateScheduler(playerService);
    audioPlayerListenerService.addListeners();
    guildAudioService.registratePlayer(guild);

    log.info("Make AudioPlayer for guild: " + guild);
  }
}
