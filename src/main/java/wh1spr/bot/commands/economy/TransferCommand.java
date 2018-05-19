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

public class TransferCommand extends Command {

	public TransferCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if(!Perm.has(Perm.ADMIN, invoker)) return;
		
		// .transfer amount from to
		if (args.size() != 3) {
			failure(message);
			return;
		}
		
		EcoInfo ei = EconomyStatus.getGuildInfo(guild);
		if (ei == null) return; //doesnt have economy
		
		double amount = 0;
		try {
			amount = Double.parseDouble(args.get(0));
		} catch(NumberFormatException e) {
			failure(message);
			channel.sendMessage("I could not read how much you were trying to transfer! Please use `.transfer X.x @from @to`").queue();
		}
		amount = Math.round(Math.abs(amount)*100)/100;
		
		if (amount == 0) {
			failure(message);
			channel.sendMessage("The minimum pay amount is 1 " + ei.getMin(1));
		}
		
		Balance from = EconomyStatus.getBalance(message.getMentionedMembers().get(0));
		Balance to = EconomyStatus.getBalance(message.getMentionedMembers().get(1));
		
		if (!from.transfer(to, amount)) {
			failure(message);
			channel.sendMessage("Transaction could not be completed.");
		} else {
			EmbedBuilder e = new EmbedBuilder().setColor(Color.GREEN)
					.setTitle("**Admin Transfer** completed.")
					.setDescription(String.format("**%s** transfered **%.2f %s** from **%s**#%s to **%s**#%s", invoker.getName(),
							amount, amount==1.00?ei.getMaj(0):ei.getMaj(1),
							from.getUser().getName(), from.getUser().getDiscriminator(),
							to.getUser().getName(), to.getUser().getDiscriminator()));
			channel.sendMessage(e.build()).queue();
		}
		
	}

	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		failure(message);
		channel.sendMessage("You can't use this command outside of an economy-enabled server.").queue();
	}
}
