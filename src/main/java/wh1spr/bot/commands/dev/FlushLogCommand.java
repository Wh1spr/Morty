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
import wh1spr.logger.LoggerCache;

public class FlushLogCommand extends Command {

	public FlushLogCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.hasSpec(Perm.OWNER, invoker) && !invoker.getId().equals(Bot.OWNER)) return;
		
		if (args.size() == 1) {
			if (LoggerCache.getLogger(name) != null) {
				LoggerCache.getLogger(name).flush();
				success(message);
				return;
			}
		}
		LoggerCache.flush();
		success(message);
	}

}
