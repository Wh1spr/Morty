package wh1spr.bot.mongodb;

import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.command.Command;

public class MongoUser extends BasicUpdateMongoItem {
	
	public MongoUser(User user) {
		this(user.getIdLong());
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

	public void setMention() {
		User u = getUser();
		this.setKey("mention", String.format("%s#%s", u.getName(), u.getDiscriminator()));			
	}
	
	public void addGuild(Guild g) {
		this.setKey("guilds." + g.getId(), new BasicDBObject());
	}
	
	public void removeGuild(Guild g) {
		this.deleteKey("guilds." + g.getId());
	}
	
	/**
	 * @param g Guild to check
	 * @return Wether or not this MongoUser has a Guild in its DB Document
	 */
	public boolean hasGuild(Guild g) {
		return getGuildDoc(g)!=null;
  }
  
	public boolean isBotBanned() {
		return getDoc().getBoolean("banned", false);
	}
	public void botBan() {this.setKey("banned", true);}
	public void botPardon() {this.deleteKey("banned");}
	
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

	protected Document getGuildDoc(Guild g) {
		return getDoc().get("guilds", Document.class).get(g.getId(), Document.class);
	}
	
	public boolean isDev() {
		Boolean dev = getDoc().getBoolean("dev");
		if (dev == null) return false;
		else return dev;
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