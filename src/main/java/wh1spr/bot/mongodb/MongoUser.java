package wh1spr.bot.mongodb;

import org.bson.Document;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public class MongoUser extends BasicUpdateMongoItem {
	
	public MongoUser(User user) {
		this(user.getId());
	}
	public MongoUser(String userId) {
		super("users", userId);
		
		if (jda.getUserById(userId)==null) { //either gone or nonexistent
			if (exists(userId)) {
				//np
			} else {
				throw new IllegalArgumentException("Given userId is unknown");
			}
		} else {
			if (!MongoDB.exists(getUser())) MongoDB.getCreator().createUser(getUser());
			if (MongoDB.isUpdated(getUser())) 
				if (!update())
					throw new Error("Could not update User " + userId + " in MongoDB.");
		}
		
	}

	public User getUser() {
		return jda.getUserById(getId());
	}
	
	/*************
	 *  GETTERS  * Getters assume that what you're doing is correct
	 *  		 * Getters use newest doc version, and only get stuff that can't be easily obtained with guild.
	 *************/
	public String getUserMention() {
		return getDoc().getString("mention");
	}
	
	public boolean hasIntro(Guild guild) {
		if (!MongoDB.getMongoGuild(guild).hasIntro()) return false;
		else if (!getDoc().get("guilds", Document.class)
				.get(guild.getId(), Document.class).containsKey("introID")){
			return false;
		} else {
			return true;
		}
	}
	
	public String getIntroId(Guild guild) {
		if (hasIntro(guild)) {
			return getDoc().get("guilds", Document.class)
					.get(guild.getId(), Document.class).getString("introID");
		} else {
			return null;
		}
	}
	
	/*************
	 *  SETTERS  * Setters assume what you're doing is correct.
	 *  		 * Setters update straight to DB. Getters use newest doc version.
	 *************/
	public void setMention() {
		this.setKey("mention", String.format("%s#%s", getUser().getName(), getUser().getDiscriminator()));			
	}
	
	@Override
	protected boolean update() {
		if (getUser() == null) return false;
		try {
			setMention();
			MongoDB.addUpdated("u" + getId());
			return true;
		} catch (Exception e) {
			log.error(e, "Couldn't update MongoUser with ID " + getId());
			return false;
		}
	}
	
	public static boolean exists(String userId) {
		return exists("users", userId);
	}
}