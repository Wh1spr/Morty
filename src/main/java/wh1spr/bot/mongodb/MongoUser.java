package wh1spr.bot.mongodb;

import org.bson.Document;

import com.mongodb.BasicDBObject;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;

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
	
	public boolean isBotBanned() {
		return getDoc().getBoolean("banned", false);
	}
	public void botBan() {this.setKey("banned", true);}
	public void botPardon() {this.deleteKey("banned");}
	
	/*************
	 *  GETTERS  * Getters assume that what you're doing is correct
	 *  		 * Getters use newest doc version, and only get stuff that can't be easily obtained with guild.
	 *************/
	
	public String getUserMention() {
		return getDoc().getString("mention");
	}
	
	public long getPermOverride() {
		if (!getDoc().containsKey("poverride")) return 0L;
		return getDoc().getLong("poverride");
	}
	
	public long getPerms(Guild g) {
		Document d = getDoc();
		if (this.getUser()==null) return getPermOverride();
		if (!g.isMember(this.getUser()) && (getPermOverride()&Command.PERM_BOT_OWNER)!=0) return 0L;
		if (isBotBanned()) return 0L;
		if (!d.containsKey(String.format("guilds.%s.perms", g.getId()))) return getPermOverride() | Command.PERM_EVERYONE;
		return d.get("guilds", Document.class).get(g.getId(), Document.class).getLong("perms") | getPermOverride();
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