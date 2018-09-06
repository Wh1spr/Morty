package wh1spr.bot.commands.mod.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.mongodb.BasicMongoItem;
import wh1spr.bot.mongodb.MongoUser;

import static com.mongodb.client.model.Updates.*;

public abstract class Action extends BasicMongoItem {

	Action(String hex, String array) {
		super(array);
		if (hex == null) throw new IllegalArgumentException("Hex cannot be null.");
		this.setId(hex);
	}
	
	public boolean isSet() {
		return getDate()!=null;
	}

	public int getHex() {
		return Integer.parseInt(getId(), 16);
	}
	public String getHexString() {
		return this.getId();
	}
	
	public String getUsername() {
		return new MongoUser(this.getDoc().getString("user")).getUserMention();
	}
	
	public String getIssuername() {
		return new MongoUser(this.getDoc().getString("by")).getUserMention();	
	}
	
	public String getReason() {
		return this.getDoc().getString("reason");
	}
	
	public String getDate() {
		return this.getDoc().getString("date");
	}
	
	protected void setItem(Guild g, User a, User by, String reason) {
		if (isSet()) throw new IllegalArgumentException("This action is already set.");
		bsonUpdates(set("guild", g.getId()), set("user", a.getId()), set("by", by.getId()),
				set("reason", reason), set("date", Tools.getDateTimeStamp()));
	}
	
	@Override
	protected boolean update() {
		return true; //nothing to update
	}

}
