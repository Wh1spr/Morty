package wh1spr.bot.mongodb;

import org.bson.BsonDouble;
import org.bson.Document;

import com.mongodb.BasicDBObject;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.bot.database.EcoInfo;

public class MongoGuild extends BasicMongoItem {

	MongoGuild(Guild guild) {
		this(guild.getId());
	}
	private MongoGuild(String guildId) {
		super("guilds"); //collection
		this.setId(guildId);
		
		if (jda.getGuildById(guildId)==null) { //either gone or nonexistent
			if (exists(guildId)) {
				
			} else {
				throw new IllegalArgumentException("Given userId is unknown");
			}
		} else {
			if (!MongoDB.exists(getGuild())) MongoDB.getCreator().createGuild(getGuild());
			if (MongoDB.isUpdated(getGuild())) 
				if (!update())
					throw new Error("Could not update Guild " + guildId + " in MongoDB.");
		}
	}
	
	public Guild getGuild() {
		return jda.getGuildById(this.getId());
	}
	
	/*************
	 *  GETTERS  * Getters assume that what you're doing is correct
	 *  		 * Getters use newest doc version, and only get stuff that can't be easily obtained with guild.
	 *************/
	public boolean hasEconomy() {
		return this.hasKey("economy");
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
		return this.hasKey("introID");
	}
	public TextChannel getIntroChannel() {
		return jda.getTextChannelById(getDoc().getString("introID"));
		//TODO check if this gives an error
	}
	
	/*************
	 *  SETTERS  * Setters assume what you're doing is correct.
	 *  		 * Setters update straight to DB.
	 *************/
	public void setName() {
		this.setKey("name", this.getGuild().getName());
	}
	public void setOwner() {
		this.setKey("ownerid", this.getGuild().getOwner());
	}
	public void updateCounts() {
		this.bsonUpdates(set("textchannels", this.getGuild().getTextChannels().size()),
						set("voicechannels", this.getGuild().getVoiceChannels().size()),
						set("members", this.getGuild().getMembers().size()));
	}
	
	public void setEconomy(EcoInfo ei) {
		this.setKey("economy", new BasicDBObject("MajSing", ei.getMaj(0))
				.append("MajMult", ei.getMaj(1))
				.append("MinSing", ei.getMin(0))
				.append("MinMult", ei.getMin(1))
				.append("startbal", new BsonDouble(ei.getStartVal()))
				.append("dailybal", new BsonDouble(ei.getDaily())));
	}
	
	public void setIntroChannel(TextChannel intro) {
		this.setKey("introID", intro.getId());
	}
	
	@Override
	protected boolean update() {
		try {
			setName();
			setOwner();
			updateCounts();
			MongoDB.addUpdated("g" + getId());
			return true;
		} catch (Exception e) {
			this.log.error(e, "Couldn't update MongoGuild with ID " + getId());
			return false;
		}
	}
	
	public static boolean exists(String guildId) {
		if (MongoDB.getDb().getCollection("guilds").find(eq("_id", guildId)).first() == null) return false;
		else return true;
	}
}
