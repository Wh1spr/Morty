package wh1spr.bot.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.dummy.Bot;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class Mongo {
	
	private static Logger log = LoggerCache.getLogger("MONGO");
	private static MongoClient client = null;
	private static MongoDatabase db = null;
	
	private static MongoCreator mc = null;
	
	public static void start() {
		log.info("Setting up database...");
		client = MongoClients.create(Main.properties.getProperty("MONGO", "mongodb://localhost"));
		db = client.getDatabase("discord");
		mc = new MongoCreator(db);
	}
	
	public static MongoDatabase getDb() {
		return db;
	}
	static MongoCreator getCreator() {
		return mc;
	}
	
	//Guild
	public static MongoGuild getMongoGuild(Guild guild) {
		if (guild==null) throw new IllegalArgumentException("Guild cannot be null!");
		if (!MongoGuild.exists(guild.getId())) mc.createGuild(guild);
		return new MongoGuild(guild);
	}
	
	//User
	public static MongoUser getMongoUser(User user) {
		if (user==null) throw new IllegalArgumentException("User cannot be null!");
		if (!MongoUser.exists(user.getId())) mc.createUser(user);
		return new MongoUser(user);
	}
	
	//Bot
	public static MongoBot getMongoBot(Bot b) {
		if (b==null) throw new IllegalArgumentException("Bot cannot be null!");
		if (!MongoBot.exists(b.getJDA().getSelfUser().getId())) mc.createBot(b);
		return new MongoBot(b);
	}
	
	public static void createItem(String collection, String id) {
		if (getDb().getCollection(collection).find(eq("_id",id)).first()!=null)
			throw new IllegalArgumentException("Illegal argument, ID " + id + " already exists in Collection " + collection);
		getDb().getCollection(collection).insertOne(new Document("_id", id));
	}
	
	
	//updated-cache (just strings)
	// prefixes u-user g-guild c-channel v-voicechannel
	// should get cleared at midnight or something, just to make sure
	private static List<String> updatedCache = new ArrayList<String>();
	static void addUpdated(String id) {
		updatedCache.add(id);
	}
	static boolean isUpdated(String id) {
		return updatedCache.contains(id);
	}
	public static boolean isUpdated(User u) {return isUpdated("u" + u.getId());}
	public static boolean isUpdated(Guild g) {return isUpdated("g" + g.getId());}
	
}
