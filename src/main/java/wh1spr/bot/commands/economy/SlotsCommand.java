package wh1spr.bot.commands.economy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

public class SlotsCommand extends Command {

	public SlotsCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
		jackpayout = 8000d;
	}
	
	private static double jackpayout = 0;
	public static void setJackpot(double val) {
		jackpayout = val;
	}
	public static double getJackpot() {
		return jackpayout;
	}
	

	@SuppressWarnings("unused")
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, invoker)) {return;}
		
		if (!EconomyStatus.isReady()) {
			warning(message);
			return;
		}
		
		if (!EconomyStatus.hasEconomy(guild)) {return;}
		
		EcoInfo ei = EconomyStatus.getGuildInfo(guild);
		
		//:apple: :tangerine: :lemon: :cherries: :watermelon: :grapes: :strawberry: :banana: and jackpot -> :moneybag:
		String[] options = ":apple: :tangerine: :lemon: :cherries: :watermelon: :grapes: :strawberry: :banana: :moneybag:".split(" ");
		
		Random r = new Random();
		
		//get 10 from balance
		Balance b = EconomyStatus.getBalance(guild.getMember(invoker));
		if (b.getBal() < 10) {
			channel.sendMessage("You do not have enough Smidgens to play this game.").queue();
			warning(message);
			return;
		} else {
			b.subtract(10d);
		}
		
		//find out what user has
		// 4 sliders, 2 the same (idfk) gets you 5 smidgens back
		// 4 sliders, 3 right (7/512) gets you 600 smidgens
		// 4 sliders correct gets you (1/512) gets you 5000 smidgens + 5 smidgens per roll
		// jackpot  (1/15000) gets you the jackpot, at least 10k
		
		boolean jackpot = false;
		boolean win2 = false;
		boolean win22 = false;
		boolean win3 = false;
		boolean win4 = false;
		if (r.nextInt(45000) < 3) jackpot = true;
		
		Integer[] resindexes = new Integer[4];
		Integer[] res = new Integer[] {0,0,0,0,0,0,0,0};
		if (!jackpot) { //now the good stuff
			
			for (int i = 0; i < 4; i++) { //4 iterations
				int next = r.nextInt(8);
				res[next]++;
				resindexes[i] = next;
			}
			
			int max = (int) Collections.max(Arrays.asList(res));
			
			if (max == 4) {
				win4 = true;
			} else if (max == 3) {
				win3 = true;
			}	else if (max == 2) {
				win2 = true;
				if (Arrays.toString(res).split("2").length > 2) win22 = true;
			}
		} else {
			resindexes = new Integer[] {8, 8, 8, 8};
		}
		
		String msg = String.format("**%s** rolled the :slot_machine: slots...\n**[ %s %s %s %s ]\n**",
				guild.getMember(invoker).getEffectiveName(),
				options[resindexes[0]], options[resindexes[1]],
				options[resindexes[2]], options[resindexes[3]]);
		
		if (jackpot) {
			b.add(jackpayout);
			jackpayout = 8000d;
			msg += String.format("and won the :tada: ***JACKPOT*** :tada: of **%d %s** \n%s", (int)(jackpayout), ei.getMaj()[1], guild.getPublicRole().getAsMention());
		} else if (win3 || win2 || win22 || win4) {
			String format = "and won! (%s/4) :tada: \nPayout is **%d %s**";
			String formatneutral = "and got something back! (%s/4)\nPayout is **%d %s**";
			jackpayout += 2;
			if (win4) {
				msg += String.format(format, "4", 4500, ei.getMaj()[1]);
				b.add(5000d);
			} else if (win3) {
				msg += String.format(format, "3", 100, ei.getMaj()[1]);
				b.add(100d);
			} else if (win22) {
				msg += String.format(formatneutral, "2*2", 10, ei.getMaj()[1]);
				b.add(10d);
			} else if (win2) {
				msg += String.format(formatneutral, "2", 3, ei.getMaj()[1]);
				b.add(3d);
			}
		} else {
			msg += "and lost!";
			jackpayout += 2d;
		}
		channel.sendMessage(msg).queue();
	}

	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		return;
	}
}
