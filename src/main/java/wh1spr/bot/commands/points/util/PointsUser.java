package wh1spr.bot.commands.points.util;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

import wh1spr.bot.mongodb.MongoDB;
import wh1spr.bot.mongodb.MongoUser;

public class PointsUser {
	
	private String module = null;
	private String type = null;
	
	PointsUser(MongoUser user, String module, String type) {
		this.mongo = user;
		
		this.module = module;
		this.type = type;
		
		if (!mongo.getDoc().containsKey(module)) {
			db.getCollection("users").updateOne(eq("_id", mongo.getId()), set(module,
					new BasicDBObject()));
		}
		
	}
	
	private MongoUser mongo = null;
	private MongoDatabase db = MongoDB.getDb();
	
	public MongoUser getMongoUser() {
		return this.mongo;
	}
	
	public Document getPointsDoc() {
		return mongo.getDoc().get(module, Document.class);
	}
	
	public boolean hasPoints() {
		return getPointsDoc().containsKey(type);
	}
	
	public int getPoints() {
		if (hasPoints()) {
			return getPointsDoc().getInteger(type);
		} else {
			return 0;
		}
	}
	
	public void setPoints(int points) {
		db.getCollection("users").updateOne(eq("_id", mongo.getId()), set(module+"."+type, points));
	}
	
	public void deletePoints() {
		db.getCollection("users").updateOne(eq("_id", mongo.getId()), unset(module+"."+type));
	}

}
