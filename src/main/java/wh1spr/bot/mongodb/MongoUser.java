package wh1spr.bot.mongodb;

import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;

public class MongoUser extends BasicUpdateMongoItem {
	
	public MongoUser(User user) {
		this(user.getIdLong());
	}
	
	@Deprecated
	public MongoUser(String userId) {
		this(Long.parseLong(userId));
	}

	public MongoUser(long userId) {
		super("users", userId);
		
		if (jda.getUserById(userId)==null) { //either gone or nonexistent
			if (!exists(userId)) throw new IllegalArgumentException("Given userId is unknown");
		} else {
			if (!exists(userId)) {
				this.createItem();
			}
			update();
		}
		
	}

	public User getUser() {
		return jda.getUserById(getIdLong());
	}
	
	/**
	 * @see MongoUser#getMention()
	 * @return
	 */
	@Deprecated
	public String getUserMention() {
		return this.getMention();
	}
	
	public String getMention() {
		return getDoc().getString("mention");
	}
	
	public void setMention() {
		User u = getUser();
		this.setKey("mention", String.format("%s#%s", u.getName(), u.getDiscriminator()));			
	}
	
	public void addGuild(Guild g) {
		//TODO
		//pushes a guildid from guilds
	}
	
	public void removeGuild(Guild g) {
		//TODO
		//removes guildid from guilds 
		//destroys data from that guild, except bans/warnings/kicks...
	}
	
	/**
	 * @param g Guild to check
	 * @return Wether or not this MongoUser has a Guild in its DB Document
	 */
	public boolean hasGuild(Guild g) {
		return getGuildDoc(g)!=null;
	}

	protected Document getGuildDoc(Guild g) {
		return getDoc().get("guilds", Document.class).get(g.getId(), Document.class);
	}
	
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
	
	@Override
	protected boolean update() {
		if (getUser() == null) return false;
		Document d = getDoc();
		if (!String.format("%s#%s", getUser().getName(), getUser().getDiscriminator()).equals(d.getString("mention"))) {
			setMention();
		}
		
		Set<String> has = getDoc().get("guilds", Document.class).keySet();
		
		jda.getMutualGuilds(getUser()).forEach(el->{
			if (!hasGuild(el)) {
				addGuild(el);
			} else {
				has.remove(el.getId());
			}
		});
		has.forEach(el->this.deleteKey("guilds." + el));
		
		return true;
	}
	
	public static boolean exists(String userId) {
		if (Tools.isPosInteger(userId)) return exists(Long.parseLong(userId));
		else return false;
	}
	
	public static boolean exists(long userId) {
		return exists("users", userId);
	}
	
	@Override
	protected void create() {
		setMention();
		this.setKey("guilds", new BasicDBObject());
	}
}