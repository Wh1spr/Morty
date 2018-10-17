package wh1spr.bot.dummy;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
		bot.getLog().info("I'm ready to rumble!");
	}
}
