package wh1spr.bot.commands.dev;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.dummy.Perm;

public class SqlExecCommand extends Command {

	public SqlExecCommand(String name, Bot b, String... aliases) {
		super(name, aliases);
		this.b = b;
	}

	private Bot b;
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.OWNER, invoker)) return;
		
		String sql = message.getContentDisplay().replaceAll("." + this.name + " ", "");
		
		if (sql.toUpperCase().contains("SELECT")) {
			failure(message);
		} else {
			try {
				b.getDb().executeUpdate(sql);
			} catch(Exception e) {
				b.getLog().error("SQLException on Query \"" + sql + "\"");
			}
		}
	}

}
