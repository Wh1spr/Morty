package wh1spr.bot.commands.music.commands;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.commands.music.GuildMusicManager;
import wh1spr.bot.commands.music.Music;
import wh1spr.bot.dummy.Perm;

public class NowCommand extends AudioCommand {

	public NowCommand(Music m, String name, String... aliases) {
		super(m, name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, guild.getMember(invoker))) {return;}
		
		GuildMusicManager mng = getMusic().getGuildM(guild);
		
		AudioPlayer player = mng.player;
	    
	    AudioTrack currentTrack = player.getPlayingTrack();
        if (currentTrack != null)
        {
            String title = currentTrack.getInfo().title;
            String position = getTimestamp(currentTrack.getPosition());
            String duration = getTimestamp(currentTrack.getDuration());

            String nowplaying = String.format("**Playing:** %s\n**Time:** [%s / %s]", title, position, duration);

            channel.sendMessage(nowplaying).queue();
        } else {
        	channel.sendMessage("The player is not currently playing anything!").queue();
        }
		
	}
	
	private static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

}
