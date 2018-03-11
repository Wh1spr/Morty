package wh1spr.bot.commands.music.commands;

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

}
