package wh1spr.bot.dummy;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import wh1spr.bot.database.Database2;
import wh1spr.bot.database.modules.EconomyModule;
import wh1spr.bot.database.modules.EntityModule;
import wh1spr.bot.database.modules.IntroModule;
import wh1spr.bot.database.modules.MaelstromModule;

/*
 * This eventhandler takes care of the 'standard' queries that each bot should execute
 */
public class AutoEventHandlerDummy extends ListenerAdapter {
	
	private final Bot bot;

	protected EntityModule dbent = null;
	protected EconomyModule dbeco = null;
	protected IntroModule dbintro = null;
	protected MaelstromModule dbmaelstrom = null;
	
	protected void db() {
		if (dbent == null) dbent = Database2.getEntity();
		if (dbeco == null) dbeco = Database2.getEco();
		if (dbintro == null) dbintro = Database2.getIntro();
		if (dbmaelstrom == null) dbmaelstrom = Database2.getMaelstrom();
	}
	
	public AutoEventHandlerDummy(Bot bot) {
		this.bot = bot;
	}
	
	public Bot getBot() {
		return this.bot;
	}
	
	public boolean canExecute(Guild guild) {
		//checks with Main if this bot can execute in the given guild
		return true; // only morty
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		bot.getLog().info("I'm ready to rumble!");
	}
}
