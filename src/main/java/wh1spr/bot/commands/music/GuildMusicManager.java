package wh1spr.bot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import net.dv8tion.jda.core.entities.Guild;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {
    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final AudioScheduler scheduler;

    /**
     * Creates a player and a track scheduler.
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(AudioPlayerManager manager, Guild guild)
    {
        player = manager.createPlayer();
        scheduler = new AudioScheduler(player, guild.getTextChannelsByName("music", true).get(0));
        player.addListener(scheduler);
        
        player.setVolume(35);
    }
    
    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioSender getSendHandler() {
    	return new AudioSender(player);
    }
}