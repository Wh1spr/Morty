package wh1spr.bot.commands.economy.util;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;

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
		JDA jda = Main.getBots().get(0).getJDA();
		this.bal = val;
		this.mem = jda.getGuildById(guildId).getMemberById(userId);
	}
	
	public Double getBal() {
		return this.bal;
	}
	
	public void add(Double val) {
		this.bal += Math.round(val*100)/100;
	}
	
	public void substract(Double val) {
		this.bal -= Math.round(val*100)/100;
	}
	
	public void set(Double val) {
		this.bal = val;
	}
	
	public static boolean transfer(Balance from, Balance to, Double val) {
		if (from==null||to==null) return false;
		if (val <= 0.00) return false;
		if (from.getBal() < val) return false;
		if (from.getGuild()!=to.getGuild()) return false;
		from.substract(val);
		to.add(val);
		return true;
	}
	
	public boolean transfer(Balance to, Double val) {
		if (this==null||to==null) return false;
		if (val <= 0.00) return false;
		if (this.getBal() < val) return false;
		if (this.getGuild()!=to.getGuild()) return false;
		this.substract(val);
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
	
}
