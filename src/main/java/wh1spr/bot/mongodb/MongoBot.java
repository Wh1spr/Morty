package wh1spr.bot.mongodb;

import org.bson.Document;

import net.dv8tion.jda.core.entities.SelfUser;
import wh1spr.bot.dummy.Bot;

public class MongoBot extends BasicUpdateMongoItem {

	private Bot b = null;
	
	public MongoBot(Bot b) {
		//only active bot should be called, so we expect JDA to be good
		super("bots", b.getJDA().getSelfUser().getId());
		this.b = b;
		
		if (!exists(getUser().getId())) {
			Mongo.createItem("bots", this.getId());
		}
		update();
	}
	
	public SelfUser getUser() {
		return jda.getSelfUser();
	}
	
	public int getNrOfGuilds() {
		return this.getDoc().getInteger("guilds", 0);
	}
	
	public void setGuilds() {
		this.setKey("guilds", jda.getGuilds().size());
	}

	public void setUsers() {
		this.setKey("users", jda.getUsers().size());
	}
	
	@Override
	protected boolean update() {
		new MongoUser(b.getJDA().getSelfUser()); //Calls update on user
		Document d = getDoc();
		if (d.getInteger("guilds")!=b.getJDA().getGuilds().size()) setGuilds();
		if (d.getInteger("users")!=b.getJDA().getUsers().size()) setUsers();
		return true;
	}
	
	public static boolean exists(String botId) {
		return exists("bots", botId);
	}
}
