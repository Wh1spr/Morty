package wh1spr.bot.commands.mod.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.MongoUser;

public class Ban extends Action {

	public Ban(String hex) {
		super(hex, "bans");
	}
	
	public Ban(String hex, Guild g, User a, User by, String reason) {
		super(hex, "bans");
		setItem(g, a,by,reason);
	}
	
	@Override
	public void setItem(Guild g, User a, User by, String reason) {
		super.setItem(g, a, by, reason);
		BanUser ma = new BanUser(a);
		new MongoUser(by);
		ma.addBan(this.getHexString());
	}
}
