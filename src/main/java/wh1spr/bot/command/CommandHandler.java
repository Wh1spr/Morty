package wh1spr.bot.command;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import wh1spr.bot.Tools;
import wh1spr.bot.database.Database2;
import wh1spr.bot.dummy.Bot;

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
		String cmdName = event.getMessage().getContentRaw().split(" ")[0].substring(PREFIX.length()).toLowerCase();
		if (registry.getRegisteredCommandsAndAliases().contains(cmdName)) {
			Command cmd = registry.getCommand(cmdName).command;
			boolean execute = !cmd.isDisabled();
			//no response if not in maelstrom and only maelstrom has been set
			if (cmd.isMaelstromOnly() && !event.getGuild().getId().equals(Bot.MAELSTROM)) execute = false;
			commandCalled(cmd, event, execute);
			if (!execute) return;
			
			List<String> args = new ArrayList<String>();
			args.addAll(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
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
			boolean execute = !cmd.isDisabled();
			//no response if not in maelstrom and only maelstrom has been set
			if (cmd.isMaelstromOnly()) execute = false;
			commandCalled(cmd, event, execute);
			if (!execute) return;
			
			List<String> args = new ArrayList<String>();
			args.addAll(Arrays.asList(event.getMessage().getContentDisplay().split(" ")));
			args.remove(0);
			cmd.onCallPrivate(event.getJDA(), event.getChannel(), event.getAuthor(), event.getMessage(), args);
		}
		
	}
	
	//booleans for another row, whenever i make that one lol
	private static final String commandCalledSql = "INSERT INTO CommandCalls Values(?,?,?,?,?,?,?)";
	private PreparedStatement commandCalledStmt = null;
	private void commandCalled(Command cmd, GuildMessageReceivedEvent e, boolean executed) {
		try {
			if (commandCalledStmt == null)
				commandCalledStmt = Database2.getConn().prepareStatement(commandCalledSql);
			
			commandCalledStmt.setString(1, Tools.getDateTimeStamp());
			commandCalledStmt.setString(2, cmd.getName());
			commandCalledStmt.setString(3, e.getAuthor().getId());
			commandCalledStmt.setString(4, e.getGuild().getId());
			commandCalledStmt.setString(5, e.getChannel().getId());
			commandCalledStmt.setString(6, e.getMessageId());
			commandCalledStmt.setString(7, e.getMessage().getContentDisplay());
			commandCalledStmt.executeUpdate();
		} catch (SQLException e1) {e1.printStackTrace();}
	}
	private void commandCalled(Command cmd, PrivateMessageReceivedEvent e, boolean executed) {
		try {
			if (commandCalledStmt == null)
				commandCalledStmt = Database2.getConn().prepareStatement(commandCalledSql);
			
			commandCalledStmt.setString(1, Tools.getDateTimeStamp());
			commandCalledStmt.setString(2, cmd.getName());
			commandCalledStmt.setString(3, e.getAuthor().getId());
			commandCalledStmt.setString(4, "NULL");
			commandCalledStmt.setString(5, e.getChannel().getId());
			commandCalledStmt.setString(6, e.getMessageId());
			commandCalledStmt.setString(7, e.getMessage().getContentDisplay());
			commandCalledStmt.executeUpdate();
		} catch (SQLException e1) {e1.printStackTrace();}
	}
	
}
