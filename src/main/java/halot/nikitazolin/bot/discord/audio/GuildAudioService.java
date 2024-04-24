package halot.nikitazolin.bot.discord.audio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import halot.nikitazolin.bot.discord.audio.player.PlayerService;
import halot.nikitazolin.bot.discord.command.BotCommandContext;
import halot.nikitazolin.bot.init.settings.model.Settings;
import halot.nikitazolin.bot.util.MessageUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@Service
@Scope("singleton")
@Getter
@Slf4j
@RequiredArgsConstructor
public class GuildAudioService {

  private final MessageUtil messageUtil;
  private final Settings settings;
  private final PlayerService playerService;
  private AudioManager audioManager;

  public void registratePlayer(Guild guild) {
    audioManager = guild.getAudioManager();
    setUpAudioSendHandler(audioManager, guild);
  }

  private void setUpAudioSendHandler(AudioManager audioManager, Guild guild) {
    if (audioManager.getSendingHandler() == null) {
      audioManager.setSendingHandler(playerService);
      log.debug("Set sending handler for guild: " + guild.getId());
    }
  }

  public boolean connectToVoiceChannel(BotCommandContext context) {
    VoiceChannel userVoiceChannel = getVoiceChannelByUser(context);

    if (checkValidChannel(context, userVoiceChannel, settings) == true) {
      audioManager.openAudioConnection(userVoiceChannel);

      log.debug("VoiceChannel is valid. Bot connected to user VoiceChannel");
      return true;
    } else {
      log.debug("VoiceChannel is invalid. Bot won't connect to VoiceChannel");
      return false;
    }
  }

  public void stopAudioSending() {
    playerService.offPlayer();
    audioManager.closeAudioConnection();
  }

  public void shutdown() {
    stopAudioSending();
    playerService.shutdownPlayer();
  }

  private VoiceChannel getVoiceChannelByUser(BotCommandContext context) {
    VoiceChannel userVoiceChannel;

    try {
      userVoiceChannel = context.getMember().getVoiceState().getChannel().asVoiceChannel();

      return userVoiceChannel;
    } catch (NullPointerException e) {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", you need to be in voice channel to use music command.");
      context.sendMessageEmbed(embed);

      log.debug("User must to be in correct voice channel to use music command.");
      return null;
    }
  }

  private boolean checkValidChannel(BotCommandContext context, VoiceChannel userVoiceChannel, Settings settings) {
    if (!checkAfkChannel(context, userVoiceChannel)) {
      return false;
    }

    if (!checkSameChannel(context, userVoiceChannel)) {
      return false;
    }

    if (!checkAllowedChannel(context, userVoiceChannel, settings)) {
      return false;
    }

    return true;
  }

  private boolean checkAfkChannel(BotCommandContext context, VoiceChannel userVoiceChannel) {
    VoiceChannel afkVoiceChannel = context.getGuild().getAfkChannel();

    if (afkVoiceChannel != null && afkVoiceChannel.equals(userVoiceChannel)) {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", this command cannot be used in the AFK channel.");
      context.sendMessageEmbed(embed);
      log.debug("User try call bot in AFK channel. User: " + context.getUser());

      return false;
    } else {
      return true;
    }
  }

  private boolean checkSameChannel(BotCommandContext context, VoiceChannel userVoiceChannel) {
    VoiceChannel botVoiceChannel = null;

    try {
      botVoiceChannel = context.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel();
    } catch (NullPointerException e) {
      log.debug("The bot is not in the voice channel");
    }

    if (botVoiceChannel != null && !botVoiceChannel.equals(userVoiceChannel)) {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", you must be in the same voice channel as the bot to use this command.");
      context.sendMessageEmbed(embed);
      log.debug("User try call bot in different channel. User:" + context.getUser());

      return false;
    } else {
      return true;
    }
  }

  private boolean checkAllowedChannel(BotCommandContext context, VoiceChannel userVoiceChannel, Settings settings) {
    List<Long> allowedVoiceChannelIds = new ArrayList<>();

    try {
      allowedVoiceChannelIds.addAll(settings.getAllowedVoiceChannelIds());
    } catch (NullPointerException e) {
      log.debug("Especial allowed voice channel not set. Bot can connect to any channel.");

      return true;
    }

    if (allowedVoiceChannelIds.isEmpty() || allowedVoiceChannelIds.contains(userVoiceChannel.getIdLong())) {
      return true;
    } else {
      EmbedBuilder embed = messageUtil.createErrorEmbed(context.getUser().getAsMention() + ", your voice channel is denied for bot.");
      context.sendMessageEmbed(embed);
      log.debug("User tried to use a command in an denied voice channel.");

      return false;
    }
  }
}
