package wh1spr.bot.dummy;

import java.util.HashMap;

import net.dv8tion.jda.core.entities.Member;

public enum Rank { // These ranks give access to functions and lift/soften limitations on commands
	CELESTIAL,
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
	SKILLFUL,
	HANDY,
	NOOB,
	OUTOFSERVER;
	
	private boolean isAbove(Rank p){
		return (compareTo(p) <= 0);
	}
	
	public static boolean has(Rank p, Member m) {
		if (override.get(m) != null) return override.get(m).isAbove(p);
		return p.isAbove(getRank(m));
	}
	
	public static boolean hasSpec(Rank p, Member m) {
		if (override.get(m) != null) return override.get(m) == p;
		return p == getRank(m);
	}
	
	private static Rank getRank(Member m) {
		if (!m.getGuild().getId().equals(Bot.MAELSTROM)) {
			return OUTOFSERVER;
		} else {
			return NOOB;
			//maelstrom
		}
	}
	
	private static HashMap<Member, Rank> override = new HashMap<Member, Rank>();
	public static void override(Member mem, Rank p) { 
		override.put(mem, p);
	}
	public static void clearOverride(Member m) {
		override.remove(m);
	}
	public static void clearAllOverrides() {
		override.clear();
	}
	
}