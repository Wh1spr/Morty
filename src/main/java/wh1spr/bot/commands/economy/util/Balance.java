package wh1spr.bot.commands.economy.util;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.Tools;

public class Balance {
	
	private Double bal = null;
	private Member mem = null;
	
	public Balance(Double val, Member mem) {
		this.mem = mem;
		this.bal = val;
	}
	public Balance(Double val, User u, Guild g) {
		this.mem = g.getMember(u);
		this.bal = val;
	}
	public Balance(Double val, String userId, String guildId) {
		JDA jda = Main.getBot().getJDA();
		this.bal = val;
		Guild g = jda.getGuildById(guildId);
		if (g==null) return;
		this.mem = g.getMemberById(userId);
	}
	
	public Double getBal() {
		return this.bal;
	}
	
	public void add(Double val) {
		this.bal += Tools.round(val);
	}
	
	public void subtract(Double val) {
		this.bal -= Tools.round(val);
	}
	
	public void set(Double val) {
		this.bal = val;
	}
	
	public static boolean transfer(Balance from, Balance to, Double val) {
		if (from==null||to==null) return false;
		if (val <= 0.00) return false;
		if (from.getBal() < val) return false;
		if (from.getGuild()!=to.getGuild()) return false;
		from.subtract(val);
		to.add(val);
		return true;
	}
	
	public boolean transfer(Balance to, Double val) {
		if (this==null||to==null) return false;
		if (val <= 0.00) return false;
		if (this.getBal() < val) return false;
		if (this.getGuild()!=to.getGuild()) return false;
		this.subtract(val);
		to.add(val);
		return true;
	}
	
	public Member getMember() {
		return this.mem;
	}
	
	public User getUser() {
		return this.mem.getUser();
	}
	
	public Guild getGuild() {
		return this.mem.getGuild();
	}
	
	@Override
	public String toString() {
		return String.format("[G:%s,U:%s,B:%.2f]", this.getGuild().getId(), this.getUser().getId(), this.getBal());
	}
	
}
