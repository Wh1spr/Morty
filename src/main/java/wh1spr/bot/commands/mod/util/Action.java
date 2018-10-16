package wh1spr.bot.commands.mod.util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.mongodb.BasicMongoItem;
import wh1spr.bot.mongodb.Mongo;
import wh1spr.bot.mongodb.MongoGuild;
import wh1spr.bot.mongodb.MongoUser;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

import org.bson.Document;

public abstract class Action extends BasicMongoItem {

	Action(String hex, String collection) {
		super(collection, hex);
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
	
	public String getGuildName() {
		return new MongoGuild(this.getDoc().getString("guild")).getName();
	}
	
	public String getGuildId() {
		return this.getDoc().getString("guild");
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
	
	protected static String getAndSetNextHex(String collection) {
		Document hexdoc = Mongo.getDb().getCollection(collection).find(eq("_id", "0")).first();
		String nextHex = null;
		if (hexdoc==null) {
			nextHex = "1";
			Mongo.getDb().getCollection(collection).insertOne(new Document("_id", "0").append("hex", nextHex));
		} else {
			nextHex = Integer.toHexString(Integer.parseInt(hexdoc.getString("hex"), 16) + 1);
			Mongo.getDb().getCollection(collection).updateOne(eq("_id", "0"), set("hex", nextHex));
		}
		return nextHex;
	}

}
