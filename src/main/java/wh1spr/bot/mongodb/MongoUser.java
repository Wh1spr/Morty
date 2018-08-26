package wh1spr.bot.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.logger.LoggerCache;

public class MongoUser {
	
	MongoUser(User user) {
		this(user.getId());
	}
	private MongoUser(String userId) {
		this.userId = userId;
		
		if (!MongoDB.exists(jda.getUserById(userId))) throw new IllegalArgumentException("User doesn't exist in db.");
		
		if (MongoDB.isUpdated("u" + userId)) 
			if (!update())
				throw new Error("Could not update User " + userId + " in MongoDB.");
	}

	private JDA jda = Main.getBot().getJDA();
	private MongoDatabase db = MongoDB.getDb();
	private String userId = null;
	public String getId() {
		return this.userId;
	}
	public User getUser() {
		return jda.getUserById(userId);
	}
	
	public Document getDoc() {
		return MongoDB.getDb().getCollection("users").find(eq("_id", getId())).first();
	}
	
	/*************
	 *  GETTERS  * Getters assume that what you're doing is correct
	 *  		 * Getters use newest doc version, and only get stuff that can't be easily obtained with guild.
	 *************/
	
	public String getUserMention() {
		return getDoc().getString("mention");
	}
	
	public boolean hasIntro(Guild guild) {
		if (!(new MongoGuild(guild)).hasIntro()) return false;
		else if (!getDoc()
				.get("guilds", Document.class)
				.get(guild.getId(), Document.class)
				.containsKey("introID")){
			return false;
		} else {
			return true;
		}
	}
	public String getIntroId(Guild guild) {
		if (hasIntro(guild)) {
			return getDoc()
					.get("guilds", Document.class)
					.get(guild.getId(), Document.class)
					.getString("introID");
		} else {
			return null;
		}
	}
	
	/*************
	 *  SETTERS  * Setters assume what you're doing is correct.
	 *  		 * Setters update straight to DB. Getters use newest doc version.
	 *************/
	public void setMention() {
		db.getCollection("users").updateOne(eq("_id", getId()), set("mention", String.format("%s#%s", getUser().getName(), getUser().getDiscriminator())));			
	}
	
	private boolean update() {
		try {
			setMention();
			MongoDB.addUpdated("u" + getId());
			return true;
		} catch (Exception e) {
			LoggerCache.getLogger("MONGO").error(e, "Couldn't update MongoGuild with ID " + getId());
			return false;
		}
	}
}