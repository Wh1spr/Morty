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

// This will need something for the different bots
public class ShutdownCommand extends Command {

	public ShutdownCommand(String name, Bot bot, String... aliases) {
		super(name, aliases);
		this.bot = bot;
		this.setMaelstromOnly(false);
	}
	
	private final Bot bot;

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.OWNER, invoker)) return;
		
		bot.shutdown();
	}

}
