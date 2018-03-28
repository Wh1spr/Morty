package wh1spr.bot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {

	public CommandHandler(String prefix, CommandRegistry registry) {
		this.PREFIX = prefix;
		this.registry = registry;
	}
	
	private final String PREFIX;
	private final CommandRegistry registry;
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return; //no response on bots
		
		//no response on something that doesnt start with the right prefix
		if (!event.getMessage().getContentStripped().startsWith(PREFIX)) return; 
		
		//if this command exists
		String cmdName = event.getMessage().getContentStripped().split(" ")[0].replaceFirst(PREFIX, "").toLowerCase();
		if (registry.getRegisteredCommandsAndAliases().contains(cmdName)) {
			Command cmd = registry.getCommand(cmdName).command;
			List<String> args = new ArrayList<String>();
			args.addAll(Arrays.asList(event.getMessage().getContentDisplay().split(" ")));
			args.remove(0);
			cmd.onCall(event.getJDA(), event.getGuild(), event.getChannel(), event.getAuthor(), event.getMessage(), args);
		}
		
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
	
		//no response one something that doesnt start with the right prefix
		if (!event.getMessage().getContentStripped().startsWith(PREFIX)) return;
		
		//if this command exists
		String cmdName = event.getMessage().getContentStripped().split(" ")[0].replaceFirst(PREFIX, "").toLowerCase();
		if (registry.getRegisteredCommandsAndAliases().contains(cmdName)) {
			Command cmd = registry.getCommand(cmdName).command;
			List<String> args = new ArrayList<String>();
			args.addAll(Arrays.asList(event.getMessage().getContentDisplay().split(" ")));
			args.remove(0);
			cmd.onCall(event.getJDA(), null, event.getChannel(), event.getAuthor(), event.getMessage(), args);
		}
		
	}
	
}
