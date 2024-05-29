package halot.nikitazolin.bot.discord.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import halot.nikitazolin.bot.discord.jda.JdaMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiscordDataReceiver {

  private final JdaMaker jdaMaker;

  public User getUserById(Long userId) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();
    User user = null;

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();

      try {
        user = jda.retrieveUserById(userId).complete();
        log.debug("Retrieved user with ID: {}", userId);
      } catch (Exception e) {
        log.debug("Failed to retrieve user with ID: {}", userId);
      }
    }

    return user;
  }

  public List<User> getUsersByIds(List<Long> userIds) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();
    List<User> users = new ArrayList<>();

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();

      for (Long userId : userIds) {
        try {
          User user = jda.retrieveUserById(userId).complete();

          if (user != null) {
            log.debug("Retrieved user with ID: {}", userId);
            users.add(user);
          }
        } catch (Exception e) {
          log.debug("Failed to retrieve user with ID: {}", userId);
        }
      }
    }

    return users;
  }

  public TextChannel getTextChannelById(Long textChannelId) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();
    TextChannel textChannel = null;

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();

      try {
        textChannel = jda.getTextChannelById(textChannelId);
        log.debug("Retrieved text channel with ID: {}", textChannelId);
      } catch (Exception e) {
        log.debug("Failed to retrieve text channel with ID: {}", textChannelId);
      }
    }

    return textChannel;
  }

  public List<TextChannel> getTextChannelsByIds(List<Long> textChannelIds) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();
    List<TextChannel> textChannels = new ArrayList<>();

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();

      for (Long textChannelId : textChannelIds) {
        try {
          TextChannel textChannel = jda.getTextChannelById(textChannelId);

          if (textChannel != null) {
            log.debug("Retrieved text channel with ID: {}", textChannelId);
            textChannels.add(textChannel);
          }
        } catch (Exception e) {
          log.debug("Failed to retrieve text channel with ID: {}", textChannelId);
        }
      }
    }

    return textChannels;
  }

  public VoiceChannel getVoiceChannelById(Long voiceChannelId) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();
    VoiceChannel voiceChannel = null;

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();

      try {
        voiceChannel = jda.getVoiceChannelById(voiceChannelId);
        log.debug("Retrieved voice channel with ID: {}", voiceChannelId);
      } catch (Exception e) {
        log.debug("Failed to retrieve voice channel with ID: {}", voiceChannelId);
      }
    }

    return voiceChannel;
  }

  public List<VoiceChannel> getVoiceChannelsByIds(List<Long> voiceChannelIds) {
    Optional<JDA> jdaOpt = jdaMaker.getJda();
    List<VoiceChannel> voiceChannels = new ArrayList<>();

    if (jdaOpt.isPresent()) {
      JDA jda = jdaOpt.get();

      for (Long voiceChannelId : voiceChannelIds) {
        try {
          VoiceChannel voiceChannel = jda.getVoiceChannelById(voiceChannelId);

          if (voiceChannel != null) {
            log.debug("Retrieved voice channel with ID: {}", voiceChannelId);
            voiceChannels.add(voiceChannel);
          }
        } catch (Exception e) {
          log.debug("Failed to retrieve voice channel with ID: {}", voiceChannelId);
        }
      }
    }

    return voiceChannels;
  }

  public VoiceChannel getVoiceChannelByMember(Member member) {
    VoiceChannel userVoiceChannel;

    try {
      userVoiceChannel = member.getVoiceState().getChannel().asVoiceChannel();
      log.debug("Retrieved voice channel by member: {}", member);

      return userVoiceChannel;
    } catch (NullPointerException e) {
      log.debug("User must to be in correct voice channel to use music command.");
      return null;
    }
  }
}
