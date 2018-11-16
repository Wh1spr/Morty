package wh1spr.bot.mongodb;

import org.bson.Document;

import com.mongodb.BasicDBObject;

import net.dv8tion.jda.core.entities.User;

public class MongoUser extends BasicUpdateMongoItem {
	
	public MongoUser(User user) {
		this(user.getId());
	}
	public MongoUser(String userId) {
		super("users", userId);
		
		if (jda.getUserById(userId)==null) { //either gone or nonexistent
			if (!exists(userId)) throw new IllegalArgumentException("Given userId is unknown");
		} else {
			if (!exists(userId)) {
				Mongo.createItem("users", this.getId());
				this.setKey("guilds", new BasicDBObject());
			}
			update();
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

//	public Document getGuildDoc(Guild g) {
//		return getDoc().get("guilds", Document.class).get(g.getId(), Document.class);
//	}
// FOR INTROUSER
//	public boolean hasIntro(Guild guild) {
//		if (!Mongo.getMongoGuild(guild).hasIntro()) return false;
//		else if (!getDoc().get("guilds", Document.class)
//				.get(guild.getId(), Document.class).containsKey("introID")){
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	public String getIntroId(Guild guild) {
//		if (hasIntro(guild)) {
//			return getDoc().get("guilds", Document.class)
//					.get(guild.getId(), Document.class).getString("introID");
//		} else {
//			return null;
//		}
//	}
	
	/*************
	 *  SETTERS  * Setters assume what you're doing is correct.
	 *  		 * Setters update straight to DB. Getters use newest doc version.
	 *************/
	public void setMention() {
		User u = getUser();
		this.setKey("mention", String.format("%s#%s", u.getName(), u.getDiscriminator()));			
	}
	
	@Override
	protected boolean update() {
		if (getUser() == null) return false;
		Document d = getDoc();
		if (!String.format("%s#%s", getUser().getName(), getUser().getDiscriminator())
				.equals(d.getString("mention"))) {
			setMention();
		}
		return true;
	}
	
	public static boolean exists(String userId) {
		return exists("users", userId);
	}
}