package wh1spr.bot.commands.economy.settings;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.economy.util.EconomyStatus;
import wh1spr.bot.database.EcoInfo;
import wh1spr.bot.dummy.Perm;

public class EcoSetupCommand extends Command {

	// .ecosetup
	public EcoSetupCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.SERVER, invoker)) {return;}
		
		if (args.size() != 6) {
			failure(message);
			return;
		}
		
		boolean hadEco = EconomyStatus.hasEconomy(guild);
		
		final double start;
		double daily = 0.0;
		try {
			start = Double.parseDouble(args.get(0));
			daily = Double.parseDouble(args.get(1));
		} catch(NumberFormatException e) {
			failure(message);
			channel.sendMessage("Make sure you use '.' as a decimal separator, and the order of your arguments is correct.").queue();
			return;
		}
		
		Main.getBots().get(0).getDb().setupEconomy(guild.getId(), args.get(2), args.get(3), args.get(4), args.get(5), start, daily);
		
		if (!hadEco) {
			//setup balances
			guild.getMembers().forEach(el-> {
				EconomyStatus.createBalance(el, start);
			});
			EconomyStatus.updateBalances();
		}
		
		EcoInfo e = EconomyStatus.getGuildInfo(guild);
		// embed to show it went through
		success(message);
		
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		failure(message);
		channel.sendMessage("You can't use this command outside of an economy-enabled server.").queue();
	}

}
