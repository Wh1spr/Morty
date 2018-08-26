package wh1spr.bot.mongodb;

import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class MongoDB {
	
	private static Logger log = LoggerCache.getLogger("MONGO");
	private static JDA jda = null;
	private static MongoClient client = null;
	private static MongoDatabase db = null;
	
	private static MongoCreator mc = null;
	
	public static void start(JDA jda) {
		MongoDB.jda = jda;
		log.info("Setting up database...");
		client = MongoClients.create(Main.properties.getProperty("MONGO", "mongodb://localhost"));
		db = client.getDatabase("discord");
		
		mc = new MongoCreator(jda, db);
		
		//consistency checks
		// really necessary? -> update on use better?
		// 	if so => gotta use a cache so you'd know if it's "fully" updated
	}
	
	public static MongoDatabase getDb() {
		return db;
	}
	static MongoCreator getCreator() {
		return mc;
	}
	
	//Guild
	public static MongoGuild getMongoGuild(Guild guild) {
		if (!exists(guild)) mc.createGuild(guild);
		return new MongoGuild(guild);
	}
	public static boolean exists(Guild guild) {
		if (db.getCollection("guilds").find(eq("_id", guild.getId())).first() == null) return false;
		else return true;
	}
	
	//User
	public static MongoUser getMongoUser(User user) {
		if (!exists(user)) mc.createUser(user);
		return new MongoUser(user);
	}
	public static boolean exists(User user) {
		if (db.getCollection("users").find(eq("_id", user.getId())).first() == null) return false;
		else return true;
	}
	
	//updated-cache (just strings)
	// prefixes u-user g-guild c-channel v-voicechannel
	// should get cleared at midnight or something
	private static List<String> updatedCache = new ArrayList<String>();
	static void addUpdated(String id) {
		updatedCache.add(id);
	}
	static boolean isUpdated(String id) {
		return updatedCache.contains(id);
	}
	public static boolean isUpdated(User u) {return isUpdated("u" + u.getId());}
	public static boolean isUpdated(Guild g) {return isUpdated("g" + g.getId());}
	public static boolean isUpdated(TextChannel t) {return isUpdated("t" + t.getId());} //intro channels should get locked on morty shutdown
//	public static boolean isUpdated(VoiceChannel v) {return isUpdated("v" + v.getId());} //idk why
	
}
