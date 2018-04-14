package wh1spr.bot.dummy;

import java.util.HashMap;
import java.util.Iterator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

public enum Perm {
	OWNER, // Wh1spr - ID 204529799912226816
	SERVER, // Server owner
	ADMIN, // Everyone with Admin Perms on any server
	BOT, // basically admin
	
	STAFF, // aka community moderator Maelstrom Only
	
	PROGRAMMER, // programmers and technical staff, get perms in programming and serveral commands
	CONTENT, // content creators, they get perms in content creators category
	
	MEMBER, // Everyone on any server
	
	TRIAL, // Maelstrom Only
	NEWB; // Everyone before accept Maelstrom Only
	
	private boolean isAbove(Perm p){
		return (compareTo(p) <= 0);
	}
	
	public static boolean has(Perm p, Member m) {
		if (override.get(m) != null) return override.get(m).isAbove(p);
		return p.isAbove(getPerm(m));
	}
	
	public static boolean hasSpec(Perm p, Member m) {
		if (override.get(m) != null) return override.get(m) == p;
		return p == getPerm(m);
	}
	
	private static Perm getPerm(Member m) {
		if (m.getUser().getId().equals(Bot.OWNER)) return OWNER;
		if (!m.getGuild().getId().equals(Bot.MAELSTROM)) {
			// standard
			if (m.isOwner()) return SERVER;
			if (m.hasPermission(Permission.ADMINISTRATOR)) return ADMIN;
			else return MEMBER;
		} else {
			Iterator<Role> iter = m.getRoles().iterator();
			while(iter.hasNext()) {
				Role n = iter.next();
				switch(n.getId()) {
				case MRoles.ADMIN:
					return SERVER;
				case MRoles.BOTS:
					return BOT;
				case MRoles.STAFF:
					return STAFF;
				case MRoles.PROGRAMMER:
					return PROGRAMMER;
				case MRoles.CONTENT_CREATOR:
					return CONTENT;
				case MRoles.MEMBER:
					return MEMBER;
				case MRoles.TRIAL:
					return TRIAL;
				case MRoles.GUEST:
					return NEWB;
				}
			}
			return NEWB;
		}
		
	}
	
	private static HashMap<Member, Perm> override = new HashMap<Member, Perm>();
	public static void override(Member mem, Perm p) { 
		override.put(mem, p);
	}
	public static void clearOverride(Member m) {
		override.remove(m);
	}
	public static void clearAllOverrides() {
		override.clear();
	}
	
}
