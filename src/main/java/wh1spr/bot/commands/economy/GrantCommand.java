package wh1spr.bot.commands.economy;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.economy.util.EconomyStatus;
import wh1spr.bot.database.EcoInfo;
import wh1spr.bot.dummy.Perm;

public class GrantCommand extends Command {

	public GrantCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if(!Perm.has(Perm.ADMIN, invoker)) return;
		
		if (!EconomyStatus.isReady()) {
			warning(message);
			return;
		}
		
		if (!EconomyStatus.hasEconomy(guild)) {return;}
		EcoInfo ei = EconomyStatus.getGuildInfo(guild);
		
		if (args.size() - message.getMentionedUsers().size() != 1) {
			failure(message); return;
		}
		
		double amount = 0;
		try {
			amount = Double.parseDouble(args.get(0));
		} catch(NumberFormatException e) {
			failure(message);
			channel.sendMessage("I could not read how much you were trying to grant! Please use `.grant X.x @user`").queue();
		}
		amount = Tools.round(amount);
		
		if (amount == 0) {
			failure(message);
			channel.sendMessage("The minimum grant amount is 1 " + ei.getMin(1));
		}
		
		EmbedBuilder e = new EmbedBuilder().setColor(Color.GREEN)
				.setDescription(message.getMentionedUsers().size()>1?
						String.format("**%s**#%s granted **%.2f %s** to these users:", invoker.getName(), invoker.getDiscriminator(), amount,amount==1.00?ei.getMaj(0):ei.getMaj(1))
						:String.format("**%s**#%s granted **%.2f %s** to ", invoker.getName(), invoker.getDiscriminator(), amount, amount==1.00?ei.getMaj(0):ei.getMaj(1)));
		
		
		if (message.getMentionedMembers().size() == 1) {
			EconomyStatus.getBalance(message.getMentionedMembers().get(0)).add(amount);
			e.appendDescription(String.format("**%s**#%s", message.getMentionedMembers().get(0).getUser().getName(),
				message.getMentionedMembers().get(0).getUser().getDiscriminator()));
			
		} else {
			for(Member to : message.getMentionedMembers()) {
				EconomyStatus.getBalance(to).add(amount);
				e.appendDescription(to.getAsMention() + "\n");
			}
		}
		channel.sendMessage(e.build()).queue();
		
		
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		return;
	}

}
