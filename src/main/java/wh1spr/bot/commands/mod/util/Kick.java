package wh1spr.bot.commands.mod.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.MongoUser;

public class Kick extends Action {

	public Kick(String hex) {
		super(hex, "kicks");
	}
	
	public Kick(String hex, Guild g, User a, User by, String reason) {
		super(hex, "kicks");
		setItem(g, a,by,reason);
	}
	
	@Override
	public void setItem(Guild g, User a, User by, String reason) {
		super.setItem(g, a, by, reason);
		KickUser ma = new KickUser(a);
		new MongoUser(by);
		ma.addKick(this.getHexString());
	}
	
	public static boolean exists(String hex) {
		return exists("kicks", hex);
	}
}
