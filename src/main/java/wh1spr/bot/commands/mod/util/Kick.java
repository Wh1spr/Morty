package wh1spr.bot.commands.mod.util;

import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.MongoUser;

public class Kick extends Action {

	public Kick(String hex) {
		super(hex, "kicks");
	}
	
	public Kick(String hex, User a, User by, String reason) {
		super(hex, "kicks");
		setItem(a,by,reason);
	}
	
	@Override
	public void setItem(User a, User by, String reason) {
		super.setItem(a, by, reason);
		KickUser ma = new KickUser(a);
		new MongoUser(by);
		ma.addKick(this.getHexString());
	}
}
