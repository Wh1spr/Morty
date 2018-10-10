package wh1spr.bot.mongodb;

import net.dv8tion.jda.core.entities.SelfUser;
import wh1spr.bot.dummy.Bot;

public class MongoBot extends BasicUpdateMongoItem {

	public MongoBot(Bot b) {
		//only active bot should be called.
		super("bots", b.getJDA().getSelfUser().getId());
		
		if (!Mongo.exists(getUser())) Mongo.getCreator().createBot(b);
		
		if (!Mongo.isUpdated(getUser())) 
			if (!update())
				throw new Error("Could not update Bot " + getId() + " in MongoDB.");
	}
	
	public SelfUser getUser() {
		return jda.getSelfUser();
	}
	
	public int getWarnHex() {
		if (hasKey("warnhex")) {
			return Integer.parseInt(getDoc().getString("warnhex"), 16);
		} else {
			return 0;
		}
	}
	public int getKickHex() {
		if (hasKey("kickhex")) {
			return Integer.parseInt(getDoc().getString("kickhex"), 16);
		} else {
			return 0;
		}
	}
	public int getBanHex() {
		if (hasKey("banhex")) {
			return Integer.parseInt(getDoc().getString("banhex"), 16);
		} else {
			return 0;
		}
	}
	public String getWarnHexString() {
		if (hasKey("warnhex")) {
			return getDoc().getString("warnhex");
		} else {
			return "0";
		}
	}
	public String getBanHexString() {
		if (hasKey("banhex")) {
			return getDoc().getString("banhex");
		} else {
			return "0";
		}
	}
	public String getKickHexString() {
		if (hasKey("kickhex")) {
			return getDoc().getString("kickhex");
		} else {
			return "0";
		}
	}
	
	public int getNrOfGuilds() {
		return this.getDoc().getInteger("guilds", 0);
	}
	
	public void setWarnHex(int nr) {
		this.setKey("warnhex", Integer.toHexString(nr));
	}
	public void setKickHex(int nr) {
		this.setKey("kickhex", Integer.toHexString(nr));
	}
	public void setBanHex(int nr) {
		this.setKey("banhex", Integer.toHexString(nr));
	}
	public void setGuilds() {
		this.setKey("guilds", jda.getGuilds().size());
	}

	@Override
	protected boolean update() {
		try {
			setGuilds();
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
