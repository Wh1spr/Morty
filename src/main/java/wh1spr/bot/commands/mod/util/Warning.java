package wh1spr.bot.commands.mod.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.MongoUser;

public class Warning extends Action {

	public Warning(String hex) {
		super(hex, "warnings");
	}
	
	public Warning(String hex, Guild g, User a, User by, String reason) {
		super(hex, "warnings");
		setItem(g, a,by,reason);
	}
	
	@Override
	public void setItem(Guild g, User a, User by, String reason) {
		super.setItem(g, a, by, reason);
		WarnUser ma = new WarnUser(a);
		new MongoUser(by);
		ma.addWarning(this.getHexString());
	}
	
	public static boolean exists(String hex) {
		return exists("warnings", hex);
	}
}
