package wh1spr.bot.commands.music;

import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import net.dv8tion.jda.core.entities.Guild;

public class Music {

	private AudioPlayerManager playerManager;
	private HashMap<String, GuildMusicManager> mngs; //id of the guild, manager for that guild
	
	public Music() {
		playerManager = new DefaultAudioPlayerManager();
		playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        mngs = new HashMap<String, GuildMusicManager>();
	}
	
	public AudioPlayerManager getManager() {
		return this.playerManager;
	}
	
	public HashMap<String, GuildMusicManager> getGuildMMgrs() {
		return this.mngs;
	}
	public GuildMusicManager getGuildM(Guild guild) {
		GuildMusicManager mng = this.getGuildMMgrs().get(guild.getId());
		if (mng == null) {
			mng = new GuildMusicManager(this.getManager(), guild);
			this.getGuildMMgrs().put(guild.getId(), mng);
		}
		return mng;
	}
}
