package wh1spr.bot.commandrework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import wh1spr.bot.dummy.Bot;

public class CommandHandler extends ListenerAdapter {

	public CommandHandler(String prefix, CommandRegistry registry) {
		if (prefix == null) throw new IllegalArgumentException("Command prefix cannot be null.");
		if (registry == null) throw new IllegalArgumentException("Registry cannot be null.");
		this.PREFIX = prefix;
		this.registry = registry;
	}
	
	private final String PREFIX;
	private final CommandRegistry registry;
	
	public String getPrefix() {
		return this.PREFIX;
	}
	public CommandRegistry getRegistry() {
		return this.registry;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		//TODO log messages
		
		if (event.getAuthor().isBot()) return; //no response on bots
		if (!event.getMessage().getContentStripped().startsWith(PREFIX)) return;

		String cmdName = event.getMessage().getContentRaw().split(" ")[0].substring(PREFIX.length()).toLowerCase();
		if (registry.getRegisteredCommandsAndAliases().contains(cmdName)) {
			Command cmd = registry.getCommand(cmdName);
			
			//conditions
			if (cmd.isDisabled()) return;
			else if (cmd.isMaelstromOnly() && !event.getGuild().getId().equals(Bot.MAELSTROM)) return;
			else if (registry.canUse(cmd.getName(), event.getMember())) return;
			
			//execution
			List<String> args = new ArrayList<String>();
			args.addAll(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
			args.remove(0);
			cmd.onCall(event.getJDA(), event.getGuild(), event.getChannel(), event.getAuthor(), event.getMessage(), args);
		}
		
	}
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
		//TODO log messages
		
		if (event.getAuthor().isBot()) return; //no response on bots
		if (!event.getMessage().getContentStripped().startsWith(PREFIX)) return;

		String cmdName = event.getMessage().getContentRaw().split(" ")[0].substring(PREFIX.length()).toLowerCase();
		if (registry.getRegisteredCommandsAndAliases().contains(cmdName)) {
			Command cmd = registry.getCommand(cmdName);
			
			//conditions
			if (cmd.isDisabled()) return;
			else if (cmd.isGuildOnly()) return;
			else if (registry.canUse(cmd.getName(), event.getAuthor())) return;
			
			//execution
			List<String> args = new ArrayList<String>();
			args.addAll(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
			args.remove(0);
			cmd.onCallPrivate(event.getJDA(), event.getChannel(), event.getAuthor(), event.getMessage(), args);
		}
		
	}

	//TODO log a message
	//=> only log commands, and removed messages BUT since removed messages cant be obtained, we'll have to log it all
	
}
