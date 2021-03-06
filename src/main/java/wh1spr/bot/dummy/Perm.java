package wh1spr.bot.dummy;

import java.util.HashMap;
import java.util.Iterator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.morty.MRoles;

public enum Perm {
	OWNER, // Wh1spr - ID 204529799912226816
	SERVER, // Server owner
	ADMIN, // Everyone with Admin Perms on any server
	BOT, // basically admin
	
	STAFF, // aka community moderator Maelstrom Only
	EO, //aka Event organizer
	
	PROGRAMMER, // programmers and technical staff, get perms in programming and serveral commands
	CONTENT, // content creators, they get perms in content creators category
	
	MEMBER, // Everyone on any server
	
	TRIAL, // Maelstrom Only
	NEWB; // Everyone before accept Maelstrom Only
	
	private boolean isAbove(Perm p){
		return (compareTo(p) <= 0);
	}
	
	public static boolean has(Perm p, User u) {
		Guild maelstrom = u.getJDA().getGuildById(Bot.MAELSTROM);
		Member m = null;
		if (maelstrom != null) m = maelstrom.getMember(u);
		if (m!=null) return has(p,m);
		else {
			if (override.get(u) != null) return override.get(u).isAbove(p);
			else if (u.getId().equals(Bot.OWNER)) return true;
			else return MEMBER.isAbove(p);
		}
	}
	
	public static boolean hasSpec(Perm p, User u) {
		Guild maelstrom = u.getJDA().getGuildById(Bot.MAELSTROM);
		Member m = null;
		if (maelstrom != null) m = maelstrom.getMember(u);
		if (m!=null) return hasSpec(p,m);
		else {
			if (override.get(u) != null) return p == override.get(u);
			else if (u.getId().equals(Bot.OWNER)) return OWNER == p;
			else return p == MEMBER;
		}
	}
	
	public static boolean has(Perm p, Member m) {
		if (override.get(m.getUser()) != null) return override.get(m.getUser()).isAbove(p);
		return getPerm(m).isAbove(p);
	}
	
	public static boolean hasSpec(Perm p, Member m) {
		if (override.get(m.getUser()) != null) return override.get(m.getUser()) == p;
		return p == getPerm(m);
	}
	
	public static Perm getPerm(Member m) {
		if (override.get(m.getUser()) != null) return override.get(m.getUser());
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
				case MRoles.EO:
					return EO;
				case MRoles.PROGRAMMER:
					return PROGRAMMER;
				case MRoles.CONTENT_CREATOR:
					return CONTENT;
				case MRoles.MEMBER:
					return MEMBER;
				case MRoles.TRIAL:
					return TRIAL;
				case MRoles.GUEST:
					return TRIAL;
				case MRoles.ACCEPT:
					return NEWB;
				}
			}
			return MEMBER;
		}
		
	}
	
	private static HashMap<User, Perm> override = new HashMap<User, Perm>();
	public static void override(User u, Perm p) { 
		override.put(u, p);
	}
	public static void clearOverride(User u) {
		override.remove(u);
	}
	public static void clearAllOverrides() {
		override.clear();
	}
	
}
