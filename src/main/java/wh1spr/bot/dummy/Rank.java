package wh1spr.bot.dummy;

import java.util.HashMap;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public enum Rank { // These ranks give access to functions and lift/soften limitations on commands
	CELESTIAL,
	DIMENSIONAL,
	DIVINE,
	GODLIKE,
	TRANSCENDENT,
	OMNISCIENT,
	MYTHICAL,
	IMMORTAL,
	DEMIGOD,
	LEGENDARY,
	HEROIC,
	FAMOUS,
	VETERAN,
	GLORIOUS,
	FANTASTIC,
	MASTER,
	SKILLFUL,
	GOOD,
	HANDY,
	NOOB,
	OUTOFSERVER;
	
	private boolean isAbove(Rank p){
		return (compareTo(p) >= 0);
	}
	
	public static boolean has(Rank p, User u) {
		Guild maelstrom = u.getJDA().getGuildById(Bot.MAELSTROM);
		Member m = null;
		if (maelstrom != null) m = maelstrom.getMember(u);
		if (m!=null) return has(p,m);
		else {
			if (override.get(u) != null) return p.isAbove(override.get(u));
			else return p.isAbove(NOOB);
		}
	}
	
	public static boolean hasSpec(Rank p, User u) {
		Guild maelstrom = u.getJDA().getGuildById(Bot.MAELSTROM);
		Member m = null;
		if (maelstrom != null) m = maelstrom.getMember(u);
		if (m!=null) return hasSpec(p,u);
		else {
			if (override.get(u) != null) return p == override.get(u);
			else return p == NOOB || p == OUTOFSERVER;
		}
	}
	
	public static boolean has(Rank p, Member m) {
		if (override.get(m.getUser()) != null) return override.get(m.getUser()).isAbove(p);
		return p.isAbove(getRank(m));
	}
	
	public static boolean hasSpec(Rank p, Member m) {
		if (override.get(m.getUser()) != null) return override.get(m.getUser()) == p;
		return p == getRank(m);
	}
	
	public static Rank getRank(Member m) {
		if (!m.getGuild().getId().equals(Bot.MAELSTROM)) {
			return OUTOFSERVER;
		} else { // Maelstrom
			return NOOB;
		}
	}
	
	private static HashMap<User, Rank> override = new HashMap<User, Rank>();
	public static void override(User u, Rank p) { 
		override.put(u, p);
	}
	public static void clearOverride(User u) {
		override.remove(u);
	}
	public static void clearAllOverrides() {
		override.clear();
	}
	
}