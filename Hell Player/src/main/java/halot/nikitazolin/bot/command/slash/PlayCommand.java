package halot.nikitazolin.bot.command.slash;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import halot.nikitazolin.bot.command.model.SlashCommand;
import halot.nikitazolin.bot.command.model.SlashCommandRecord;
import halot.nikitazolin.bot.player.BotAudioHandler;
import halot.nikitazolin.bot.util.MessageUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Component
public class PlayCommand extends SlashCommand {

  @Override
  public String name() {
    return "play";
  }

  @Override
  public String description() {
    return "Start playing music from link";
  }

  @Override
  public String requiredRole() {
    return null;
  }

  @Override
  public Permission neededPermission() {
    return Permission.USE_APPLICATION_COMMANDS;
  }

  @Override
  public boolean guildOnly() {
    return true;
  }

  @Override
  public OptionData[] options() {
    return new OptionData[] {};
  }

  @Override
  public void execute(SlashCommandRecord info) {
    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    
    Guild guild = info.event().getGuild();
    Member member = info.event().getMember();

    if (guild == null || member == null) {
      return;
    }
    
    VoiceChannel voiceChannel = guild.getVoiceChannelsByName("General", true).get(0);
    
    if (voiceChannel == null) {
      info.event().reply("You need to be in a voice channel to use this command.").queue();
      return;
    }
    
//    AudioManager audioManager = guild.getAudioManager();
//    audioManager.openAudioConnection(voiceChannel);
//    AudioPlayer player = playerManager.createPlayer();
//    audioManager.setSendingHandler(new BotAudioHandler(player));
    
    guild.getAudioManager().openAudioConnection(voiceChannel);
    AudioPlayer player = playerManager.createPlayer();
    BotAudioHandler audioHandler = new BotAudioHandler(player);
    guild.getAudioManager().setSendingHandler(audioHandler);

    System.out.println("Status: " + guild.getAudioManager().getConnectionStatus());
    
    String trackUrl = "D:\\Music\\Folders\\2023\\30 Seconds To Mars - Attack.mp3";

    playerManager.loadItem(trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        player.playTrack(track);
        info.event().reply("Now playing: " + track.getInfo().title).queue();
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
      }

      @Override
      public void noMatches() {
        info.event().reply("Track not found by URL: " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        info.event().reply("Could not play: " + exception.getMessage()).queue();
      }
    });
  }
}
