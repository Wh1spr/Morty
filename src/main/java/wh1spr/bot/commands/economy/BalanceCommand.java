package wh1spr.bot.commands.economy;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.economy.util.Balance;
import wh1spr.bot.commands.economy.util.EconomyStatus;
import wh1spr.bot.database.EcoInfo;
import wh1spr.bot.dummy.Perm;

public class BalanceCommand extends Command {

	public BalanceCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if(!Perm.has(Perm.MEMBER, invoker)) return;
		if (!EconomyStatus.hasEconomy(guild)) {return;}
		
		EcoInfo ei = EconomyStatus.getGuildInfo(guild);
		Balance b = EconomyStatus.getBalance(guild, invoker);
		
		EmbedBuilder e = new EmbedBuilder().setColor(Color.GREEN);
		
		String title = "You currently have";
		
		if (b.getBal() >= 1) {
			title += String.format(" **%s %s**", Long.toString(Math.round(b.getBal())), Math.round(b.getBal())==1?ei.getMaj(0):ei.getMaj(1));
		}
		if ((b.getBal()*100) - Math.round(b.getBal())*100 > 0) {
			int cents = (int) ((b.getBal()*100) - Math.round(b.getBal())*100);
			title += String.format(" **%d %s**", cents, cents==1?ei.getMin(0):ei.getMin(1));
		}
		
		e.setTitle(title);
		channel.sendMessage(e.build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		failure(message);
		channel.sendMessage("You can't use this command outside of an economy-enabled server.").queue();
	}

}
