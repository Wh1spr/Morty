package wh1spr.bot.commands.dev;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.database.Database2;
import wh1spr.bot.dummy.Perm;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class SqlExecCommand extends Command {

	public SqlExecCommand(String name, String... aliases) {
		super(name, aliases);
	}
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.OWNER, invoker)) return;
		
		Logger log = LoggerCache.getLogger("SQL-CMD");
		
		String sql = message.getContentDisplay().replaceAll("." + this.name + " ", "");
		
		if (sql.toUpperCase().contains("SELECT")) {
			failure(message);
		} else {
			try {
				Database2.executeUpdate(sql);
			} catch(Exception e) {
				log.error("SQLException on Query \"" + sql + "\"");
			}
		}
	}

}
