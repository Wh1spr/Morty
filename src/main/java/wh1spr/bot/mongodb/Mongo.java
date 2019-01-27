package wh1spr.bot.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

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
}
