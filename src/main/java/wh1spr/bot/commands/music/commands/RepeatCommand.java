package wh1spr.bot.commands.music.commands;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.commands.music.GuildMusicManager;
import wh1spr.bot.commands.music.Music;

public class RepeatCommand extends AudioCommand {

	public RepeatCommand(Music m, String name, String... aliases) {
		super(m, name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		GuildMusicManager mng = getMusic().getGuildM(guild);
		
		mng.scheduler.setRepeating(!mng.scheduler.isRepeating());
		if (mng.scheduler.isRepeating()) channel.sendMessage("Player set to **Repeating** mode.").queue();
		else channel.sendMessage("Player set to **Continue** mode.").queue();
	}
}
