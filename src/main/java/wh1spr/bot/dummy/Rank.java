package wh1spr.bot.dummy;

import java.util.HashMap;
import java.util.Iterator;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

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
		} else { // Maelstrom
			Iterator<Role> iter = m.getRoles().iterator();
			while(iter.hasNext()) {
				Role n = iter.next();
				switch(n.getId()) {
				case MRoles.CELESTIAL:
					return CELESTIAL;
				case MRoles.DIMENSIONAL:
					return DIMENSIONAL;
				case MRoles.DIVINE:
					return DIVINE;
				case MRoles.GODLIKE:
					return GODLIKE;
				case MRoles.TRANSCENDENT:
					return TRANSCENDENT;
				case MRoles.OMNISCIENT:
					return OMNISCIENT;
				case MRoles.MYTHICAL:
					return MYTHICAL;
				case MRoles.IMMORTAL:
					return IMMORTAL;
				case MRoles.DEMIGOD:
					return DEMIGOD;
				case MRoles.LEGENDARY:
					return LEGENDARY;
				case MRoles.HEROIC:
					return HEROIC;
				case MRoles.FAMOUS:
					return FAMOUS;
				case MRoles.VETERAN:
					return VETERAN;
				case MRoles.GLORIOUS:
					return GLORIOUS;
				case MRoles.FANTASTIC:
					return FANTASTIC;
				case MRoles.MASTER:
					return MASTER;
				case MRoles.SKILLFUL:
					return SKILLFUL;
				case MRoles.GOOD:
					return GOOD;
				case MRoles.HANDY:
					return HANDY;
				case MRoles.NOOB:
					return NOOB;
				}
			}
			return NOOB;
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