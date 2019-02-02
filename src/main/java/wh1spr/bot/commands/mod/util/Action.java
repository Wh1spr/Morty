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
		super(collection, Long.parseLong(hex, 16));
	}
	
	public boolean isSet() {
		return getDate()!=null;
	}

	public long getHexLong() {
		return this.getIdLong();
	}
	
	public String getHexString() {
		return Long.toHexString(this.getIdLong());
	}
	
	public String getUsername() {
		return new MongoUser(this.getDoc().getLong("user")).getMention();
	}
	
	public String getIssuername() {
		return new MongoUser(this.getDoc().getLong("by")).getMention();	
	}
	
	public String getGuildName() {
		return new MongoGuild(this.getDoc().getLong("guild")).getName();
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
		bsonUpdates(set("guild", g.getIdLong()), set("user", a.getIdLong()), set("by", by.getIdLong()),
				set("reason", reason), set("date", Tools.getDateTimeStamp()));
	}
	
	protected static String getAndSetNextHex(String collection) {
		Document hexdoc = Mongo.getDb().getCollection(collection).find(eq("_id", 0L)).first();
		if (hexdoc==null) {
			Mongo.getDb().getCollection(collection).insertOne(new Document("_id", 0L).append("hex", 1L));
		} else {
			Mongo.getDb().getCollection(collection).updateOne(eq("_id", 0L), inc("hex",1L));
		}
		return Long.toString(Mongo.getDb().getCollection(collection).find(eq("_id", 0L)).first().getLong("hex"));
	}
	
	@Override
	protected void create() {} //not needed, done by Action#setItem()

}
