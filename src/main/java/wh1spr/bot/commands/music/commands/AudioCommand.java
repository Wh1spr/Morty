package wh1spr.bot.commands.music.commands;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.music.Music;

abstract class AudioCommand extends Command {

	private Music music = null;
	
	public AudioCommand(Music music, String name, String... aliases) {
		super(name, aliases);
		this.music = music;
	}
	
	public Music getMusic() {
		return this.music;
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {}

}
