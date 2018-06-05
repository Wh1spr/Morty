package wh1spr.bot.commands.economy.settings;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.economy.util.EconomyStatus;
import wh1spr.bot.database.Database2;
import wh1spr.bot.database.modules.EconomyModule;
import wh1spr.bot.dummy.Perm;

public class EcoSetupCommand extends Command {

	// .ecosetup
	public EcoSetupCommand(String name, String... aliases) {
		super(name, aliases);
		this.eco = Database2.getEco();
		this.setMaelstromOnly(false);
	}

	private EconomyModule eco = null;
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.SERVER, invoker)) {return;}
		
		if (!EconomyStatus.isReady()) {
			warning(message);
			return;
		}
		
		if (args.size() != 6) {
			failure(message);
			return;
		}
		
		boolean hadEco = EconomyStatus.hasEconomy(guild);
		
		double start;
		double daily;
		try {
			start = Double.parseDouble(args.get(0));
			daily = Double.parseDouble(args.get(1));
			
			start = Tools.round(start);
			daily = Tools.round(daily);
		} catch(NumberFormatException e) {
			failure(message);
			channel.sendMessage("Make sure you use '.' as a decimal separator, and the order of your arguments is correct.").queue();
			return;
		}
		String majSing = args.get(2);
		String majMult = args.get(3);
		String minSing = args.get(4);
		String minMult = args.get(5);
		String s = "**Currency names**\n\n• Major Singular\n	*%s*"
				+ "\n\n• Major Plural\n	*%s*"
				+ "\n\n• Minor Singular\n	*%s*"
				+ "\n\n• Minor Plural\n	*%s*"
				+ "\n\n• Daily Balance\n	*%.2f*"
				+ "\n\n• Start Balance\n	*%.2f*";
		
		if (majSing.toUpperCase().equals("DEF")) majSing = "Dollar";
		if (majMult.toUpperCase().equals("DEF")) majMult = "Dollars";
		if (minSing.toUpperCase().equals("DEF")) minSing = "Cent";
		if (majSing.toUpperCase().equals("DEF")) minMult = "Cents";
		
		s = String.format(s, majSing, majMult, minSing, majSing, daily, start);
		
		if(!eco.setupEconomy(guild.getId(), majSing, majMult, minSing, minMult, start, daily)){
			warning(message);
			return;
		}
		
		final double startcopy = start;
		if (!hadEco) {
			//setup balances
			guild.getMembers().forEach(el-> {
				if (!el.getUser().isBot())
					EconomyStatus.createBalance(el, startcopy);
			});
			EconomyStatus.updateBalances();
		}
		// embed to show it went through
		EmbedBuilder e = new EmbedBuilder().setColor(Color.GREEN)
				.setTitle("**Economy Setup Complete!**")
				.setDescription(s);
		
		channel.sendMessage(e.build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		failure(message);
		channel.sendMessage("You can't use this command outside of an economy-enabled server.").queue();
	}

}
