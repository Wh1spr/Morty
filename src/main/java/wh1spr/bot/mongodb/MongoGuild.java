package wh1spr.bot.mongodb;

import org.bson.BsonDouble;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.bot.Main;
import wh1spr.bot.database.EcoInfo;
import wh1spr.logger.LoggerCache;

public class MongoGuild {

	MongoGuild(Guild guild) {
		this(guild.getId());
	}
	private MongoGuild(String guildId) {
		this.guildId = guildId;
		
		if (!MongoDB.exists(jda.getGuildById(guildId))) throw new IllegalArgumentException("Guild doesn't exist in db.");
		
		if (MongoDB.isUpdated("g" + guildId)) 
			if (!update())
				throw new Error("Could not update guild " + guildId + " in MongoDB.");
	}

	private JDA jda = Main.getBot().getJDA();
	private MongoDatabase db = MongoDB.getDb();
	private String guildId = null;
	public String getId() {
		return this.guildId;
	}
	public Guild getGuild() {
		return jda.getGuildById(guildId);
	}
	
	public final Document getDoc() {
		return MongoDB.getDb().getCollection("guilds").find(eq("_id", getId())).first();
	}
	
	/*************
	 *  GETTERS  * Getters assume that what you're doing is correct
	 *  		 * Getters use newest doc version, and only get stuff that can't be easily obtained with guild.
	 *************/
	public boolean hasEconomy() {
		return getDoc().containsKey("economy");
	}
	public EcoInfo getEconomy() {
		if (hasEconomy()) {
			Document d = getDoc();
			return new EcoInfo(getId(), d.getString("MajSing"), d.getString("MajMult"),
					d.getString("MinSing"), d.getString("MinMult"), d.getDouble("startbal"), d.getDouble("dailybal"));
		} else {
			return null;
		}
	}
	
	public boolean hasIntro() {
		return getDoc().containsKey("introID");
	}
	public TextChannel getIntroChannel() {
		return jda.getTextChannelById(getDoc().getString("introID"));
	}
	
	/*************
	 *  SETTERS  * Setters assume what you're doing is correct.
	 *  		 * Setters update straight to DB. Getters use newest doc version.
	 *************/
	public void setName() {
		db.getCollection("guilds").updateOne(eq("_id", getId()), set("name", this.getGuild().getName()));
	}
	public void setOwner() {
		db.getCollection("guilds").updateOne(eq("_id", getId()), set("ownerid", this.getGuild().getOwner()));
	}
	public void updateCounts() {
		db.getCollection("guilds").updateOne(eq("_id", getId()), 
				combine(set("textchannels", this.getGuild().getTextChannels().size()),
						set("voicechannels", this.getGuild().getVoiceChannels().size()),
						set("members", this.getGuild().getMembers().size())));
	}
	
	public void setEconomy(EcoInfo ei) {
		db.getCollection("guilds").updateOne(eq("_id", getId()),
				set("economy", new BasicDBObject("MajSing", ei.getMaj(0))
				.append("MajMult", ei.getMaj(1))
				.append("MinSing", ei.getMin(0))
				.append("MinMult", ei.getMin(1))
				.append("startbal", new BsonDouble(ei.getStartVal()))
				.append("dailybal", new BsonDouble(ei.getDaily()))));
	}
	
	public void setIntroChannel(TextChannel intro) {
		db.getCollection("guilds").updateOne(eq("_id", getId()), 
				set("introID", intro.getId()));
	}
	
	private boolean update() {
		try {
			setName();
			setOwner();
			updateCounts();
			MongoDB.addUpdated("g" + getId());
			return true;
		} catch (Exception e) {
			LoggerCache.getLogger("MONGO").error(e, "Couldn't update MongoGuild with ID " + getId());
			return false;
		}
	}
}
