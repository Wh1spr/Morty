package wh1spr.bot.commands.economy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import wh1spr.bot.database.Database2;
import wh1spr.bot.database.EcoInfo;
import wh1spr.bot.database.modules.EconomyModule;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class EconomyStatus {
	
	private static Logger log = null;
	
	private static Timer t = null;
	private static EconomyModule eco = null;
	
	private static boolean isReady = false;
	
	public static void start() {
		EconomyStatus.log = LoggerCache.getLogger("ECOSTATUS");
		isReady = false;
		
		log.info("Getting Database EconomyModule...");
		EconomyStatus.eco = Database2.getEco();
		if (!eco.isReady()) {
			log.error("EconomyModule isn't ready? Continuing without economy.");
			return;
		}
		
		log.info("Setting up balances...");
		updateBalances();
		balances = new HashMap<String, Balance>();
		eco.getBalances().forEach(el->{
			if (el.getMember() != null)
				balances.put(el.getGuild().getId() + "-" + el.getUser().getId(), el);
		});
		log.info(String.format("%d balances registered.", balances.size()));
		
		log.info("Setting up Balance Update Cycle Timer...");
		if (t!=null) t.cancel();
		t = new Timer();
		t.schedule(new BalUpdateCycle(), 120000, 600000);
		log.info("Timer setup finished.");
		
		log.info("EconomyStatus is ready to rumble!");
		isReady = true;
	}
	
	public static boolean isReady() {
		return EconomyStatus.isReady;
	}
	
	//Set up via Database2
	private static HashMap<String, Balance> balances = null;
	
	public static Balance getBalance(String guildId, String userId) {
		if (!isReady()) return null;
		return balances.get(guildId + "-" + userId);
	}
	public static Balance getBalance(Guild guild, User user) {return getBalance(guild.getId(), user.getId());}
	public static Balance getBalance(Member m) {return getBalance(m.getGuild().getId(), m.getUser().getId());}
	public static Map<String, Balance> getBalances() {return balances;}
	public static synchronized Balance createBalance(Member m, double val) {
		if (!isReady()) return null;
		Balance b = new Balance(val, m);
		balances.put(m.getGuild().getId() + "-" + m.getUser().getId(), b);
		return b;
	}
	
	//all these calls will return false or null if the economy module is not ready
	public static boolean hasEconomy(Guild guild) {return guild==null?false:eco.hasEconomy(guild.getId());}
	public static boolean hasEconomy(String guildid) {return guildid==null?false:eco.hasEconomy(guildid);}
	public static EcoInfo getGuildInfo(Guild guild) {return guild==null?null:eco.getGuildInfo(guild.getId());}
	public static EcoInfo getGuildInfo(String guildid) {return guildid==null?null:eco.getGuildInfo(guildid);}
	
	public static void updateBalances() {
		if (!isReady()) return;
		if (balances == null) return;
		else if (balances.isEmpty()) return;
		else eco.updateBalances(balances.values());
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
