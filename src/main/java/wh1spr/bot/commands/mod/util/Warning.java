package wh1spr.bot.commands.mod.util;

import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.mongodb.BasicMongoItem;

import static com.mongodb.client.model.Updates.*;

public class Warning extends BasicMongoItem {

	Warning(String hex) {
		super("warnings");
		if (hex == null) throw new IllegalArgumentException("Hex cannot be null.");
		this.setId(hex);
	}

	public int getHex() {
		return Integer.parseInt(getId(), 16);
	}
	public String getHexString() {
		return this.getId();
	}
	
	public User getUser() {
		return null;
	}
	
	public User getIssuer() {
		User u = jda.getUserById(this.getDoc().getString("by")); //keep name?
		if (u==null) {
			
		}
		return null;
	}
	
	public String getReason() {
		return this.getDoc().getString("reason");
	}
	
	public String getDate() {
		return this.getDoc().getString("date");
	}
	
	public void setItem(User a, User by, String reason) {
		bsonUpdates(set("user", a.getId()), set("by", by.getId()), set("reason", reason), set("date", Tools.getDateTimeStamp()));
	}
	
	@Override
	protected boolean update() {
		return true; //nothing to update
	}

}
