package wh1spr.bot.commands.economy;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.economy.util.Balance;
import wh1spr.bot.commands.economy.util.EconomyStatus;
import wh1spr.bot.database.EcoInfo;
import wh1spr.bot.dummy.Perm;

public class DailyCommand extends Command {

	public DailyCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
		dailies = new HashSet<Member>();
		t = new Timer();
		
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, 23);
		today.set(Calendar.MINUTE, 59);
		today.set(Calendar.SECOND, 59);

		t.schedule(new DailyCycle(), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
	}
	//gotta put it in database so multiple start/stop won't interfere

	private Timer t = null;
	private HashSet<Member> dailies = null;
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, invoker)) return;
		
		EcoInfo ei = EconomyStatus.getGuildInfo(guild);
		if (ei == null) return; //doesnt have economy
		if (ei.getDaily() <= 0) return;
		if (dailies.contains(guild.getMember(invoker))) return;
		
		dailies.add(guild.getMember(invoker));
		
		Balance b = EconomyStatus.getBalance(guild, invoker);
		b.add(ei.getDaily());
		
		channel.sendMessage(String.format(":white_check_mark: You have been awarded your daily **%.2f %s**.", ei.getDaily(), ei.getDaily()==1.00?ei.getMaj(0):ei.getMaj(1))).queue();
		
	}

	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		failure(message);
		channel.sendMessage("You can't use this command outside of an economy-enabled server.").queue();
	}

	private class DailyCycle extends TimerTask {
		@Override
		public void run() {
			dailies.clear();
		}
	}
}
