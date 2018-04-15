package wh1spr.bot.commands.music.commands;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
//import wh1spr.bot.commands.music.AudioScheduler;
import wh1spr.bot.commands.music.GuildMusicManager;
import wh1spr.bot.commands.music.Music;
import wh1spr.bot.dummy.Perm;

public class PauseCommand extends AudioCommand {

	public PauseCommand(Music m, String name, String... aliases) {
		super(m, name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, guild.getMember(invoker))) {return;}
		
		GuildMusicManager mng = getMusic().getGuildM(guild);
		
		AudioPlayer player = mng.player;
//	    AudioScheduler scheduler = mng.scheduler;
	    
	    if (player.getPlayingTrack() == null) player.setPaused(true);
		player.setPaused(!player.isPaused());
		
		if (player.isPaused()) channel.sendMessage("Player set to **Paused**.").queue();
		else channel.sendMessage("Player set to **Unpaused**.").queue();
	}

}