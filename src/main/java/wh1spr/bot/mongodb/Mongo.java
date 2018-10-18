package wh1spr.bot.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

import wh1spr.bot.Main;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class Mongo {
	
	private static Logger log = LoggerCache.getLogger("MONGO");
	private static MongoClient client = null;
	private static MongoDatabase db = null;
	
	public static void start() {
		log.info("Setting up database...");
		client = MongoClients.create(Main.properties.getProperty("MONGO", "mongodb://localhost"));
		db = client.getDatabase("discord");
	}
	
	public static MongoDatabase getDb() {
		return db;
	}
	
	public static void createItem(String collection, String id) {
		if (getDb().getCollection(collection).find(eq("_id",id)).first()!=null)
			throw new IllegalArgumentException("Illegal argument, ID " + id + " already exists in Collection " + collection);
		getDb().getCollection(collection).insertOne(new Document("_id", id));
	}
}
