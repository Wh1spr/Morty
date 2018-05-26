package wh1spr.bot.commands.economy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import wh1spr.bot.database.EcoInfo;
import wh1spr.bot.dummy.Bot;
import wh1spr.logger.LoggerCache;

public class EconomyStatus {

	//TODO put ecoInfos in here, just like balances
	
	private static Bot bot = null;
	private static Timer t = null;
	
	public static void start(Bot bot) {
		EconomyStatus.bot = bot;
		if (balances==null) {
			balances = new HashMap<String, Balance>();
			bot.getDb().getBalances().forEach(el->{
				if (el.getMember() != null)
					balances.put(el.getGuild().getId() + "-" + el.getUser().getId(), el);
			});
		}
		if (t==null) {
			t = new Timer();
			t.schedule(new BalUpdateCycle(), 120000, 600000);
		}
	}
	
	//Set up via Database2
	private static HashMap<String, Balance> balances = null;
	
	public static Balance getBalance(String guildId, String userId) {
		return balances.get(guildId + "-" + userId);
	}
	public static Balance getBalance(Guild guild, User user) {return getBalance(guild.getId(), user.getId());}
	public static Balance getBalance(Member m) {return getBalance(m.getGuild().getId(), m.getUser().getId());}
	public static Map<String, Balance> getBalances() {
		return balances;
	}
	public static synchronized Balance createBalance(Member m, double val) {
		Balance b = new Balance(val, m);
		balances.put(m.getGuild().getId() + "-" + m.getUser().getId(), b);
		return b;
	}
	
	public static boolean hasEconomy(Guild guild) {return guild==null?false:bot.getDb().hasEconomy(guild.getId());}
	public static boolean hasEconomy(String guildid) {return guildid==null?false:bot.getDb().hasEconomy(guildid);}
	public static EcoInfo getGuildInfo(Guild guild) {return guild==null?null:bot.getDb().getGuildInfo(guild.getId());}
	public static EcoInfo getGuildInfo(String guildid) {return guildid==null?null:bot.getDb().getGuildInfo(guildid);}
	
	public static void updateBalances() {
		bot.getDb().updateBalances(balances.values());
	}
	
	public static void shutdown() {
		LoggerCache.getLogger("MAIN").info("Shutting down economy.");
		t.cancel();
		updateBalances();
	}
	
	private static class BalUpdateCycle extends TimerTask {
		@Override
		public void run() {
			updateBalances();
		}
	}
}
