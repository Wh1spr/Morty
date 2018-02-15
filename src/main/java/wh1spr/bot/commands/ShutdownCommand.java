package wh1spr.bot.commands;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.morty.Permission;

public class ShutdownCommand extends Command {

	public ShutdownCommand(String name, Bot bot, String... aliases) {
		super(name, aliases);
		this.bot = bot;
	}
	
	private final Bot bot;

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker.getUser(), true)) return;
		
		bot.shutdown();
	}

}
