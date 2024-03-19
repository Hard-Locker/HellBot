package halot.nikitazolin.bot.audio.player;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope("singleton")
@Slf4j
@RequiredArgsConstructor
public class AudioPlayerListenerService {
  
  private final IPlayerService playerService;
  private final TrackScheduler trackScheduler;
  
  public void addListeners() {
    playerService.getAudioPlayer().addListener(trackScheduler);
    
    log.info("For audio player was added listener: " + trackScheduler.toString());
  }

}
