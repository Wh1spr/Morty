package wh1spr.bot.mongodb;

import net.dv8tion.jda.core.entities.SelfUser;
import wh1spr.bot.dummy.Bot;

public class MongoBot extends BasicUpdateMongoItem {

	public MongoBot(Bot b) {
		//only active bot should be called.
		super("bots", b.getJDA().getSelfUser().getId());
		
		if (!exists(getUser().getId())) Mongo.getCreator().createBot(b);
		
		if (!Mongo.isUpdated(getUser())) 
			if (!update())
				throw new Error("Could not update Bot " + getId() + " in MongoDB.");
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
		try {
			setGuilds();
			setUsers();
			Mongo.addUpdated("u" + getId());
			return true;
		} catch (Exception e) {
			log.error(e, "Couldn't update MongoBot with ID " + getId());
			return false;
		}
	}
	
	public static boolean exists(String botId) {
		return exists("bots", botId);
	}
}
