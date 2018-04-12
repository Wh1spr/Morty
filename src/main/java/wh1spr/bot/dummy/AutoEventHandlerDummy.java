package wh1spr.bot.dummy;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import wh1spr.bot.dummy.database.DatabaseDummy;

/*
 * This eventhandler takes care of the 'standard' queries that each bot should execute
 */
public class AutoEventHandlerDummy extends ListenerAdapter {
	
	private final Bot bot;

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
		// UPDATE THE DB WITH EVERYTHING
		DatabaseDummy db = getBot().getDb();
		
		// we don't use getBot().getJDA() here because at this point
		// the jda variable hasn't been set yet, it happens right after.
		for(Guild g : event.getJDA().getGuilds()) {
			if (!canExecute(g)) continue;
			db.addGuild(g);
		}
	}
	
	
}
