package wh1spr.bot.commands.music.commands;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.commands.music.GuildMusicManager;
import wh1spr.bot.commands.music.Music;
import wh1spr.bot.dummy.Perm;

public class VolumeCommand extends AudioCommand {

	public VolumeCommand(Music m, String name, String... aliases) {
		super(m, name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, guild.getMember(invoker))) {return;}
		
		GuildMusicManager mng = getMusic().getGuildM(guild);
		
		AudioPlayer player = mng.player;
	    
	    if (args.isEmpty()) {
			channel.sendMessage(String.format("The current volume is **%d/150**", player.getVolume())).queue();
		} else {
			int vol = player.getVolume();
			try {
				vol = Integer.valueOf(args.get(0));
			} catch (Exception e) {
				//nothing
			}
			player.setVolume(vol);
		}
	}

}
