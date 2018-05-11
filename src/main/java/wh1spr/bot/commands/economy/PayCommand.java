package wh1spr.bot.commands.economy;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.economy.util.EconomyStatus;
import wh1spr.bot.database.EcoInfo;
import wh1spr.bot.dummy.Perm;

public class PayCommand extends Command {

	public PayCommand(String name, String... aliases) {
		super(name, aliases);
	}
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, invoker)) {return;}
		if (!EconomyStatus.hasEconomy(guild)) {return;}
		
		EcoInfo ei = EconomyStatus.getGuildInfo(guild);
		
		// get to know how much and whom to pay
		// transfer
		// add to eco log
		// let them know it went through
		
		
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		failure(message);
		channel.sendMessage("You can't use this command outside of an economy-enabled server.").queue();
	}

}
