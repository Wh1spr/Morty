package wh1spr.bot.mongodb;

import static com.mongodb.client.model.Updates.*;

import java.time.OffsetDateTime;

import org.bson.Document;

import net.dv8tion.jda.core.entities.Guild;

public class MongoGuild extends BasicUpdateMongoItem {

	public MongoGuild(Guild guild) {
		this(guild.getId());
	}
	public MongoGuild(String guildId) {
		super("guilds", guildId);
		
		if (jda.getGuildById(guildId)==null) { //either gone or nonexistent
			if (!exists(guildId)) throw new IllegalArgumentException("Given guildId is unknown");
		} else {
			if (!exists(guildId)) {
				Mongo.createItem("guilds", this.getId());
				this.setKey("creationtime", getGuild().getCreationTime().toString());
			}
			update();
		}
	}
	
	public Guild getGuild() {
		return jda.getGuildById(this.getId());
	}
	
	/*************
	 *  GETTERS  * Getters assume that what you're doing is correct
	 *  		 * Getters use newest doc version, and only get stuff that can't be easily obtained with guild.
	 *************/
	
	//For economyguild => probs gonna get replaced with PointsGuild
//	public boolean hasEconomy() {
//		return this.hasKey("economy");
//	}
//	public EcoInfo getEconomy() {
//		if (hasEconomy()) {
//			Document d = getDoc();
//			return new EcoInfo(getId(), d.getString("MajSing"), d.getString("MajMult"),
//					d.getString("MinSing"), d.getString("MinMult"), d.getDouble("startbal"), d.getDouble("dailybal"));
//		} else {
//			return null;
//		}
//	}
	
	//for IntroGuild
//	public boolean hasIntro() {
//		return this.hasKey("introID");
//	}
//	public TextChannel getIntroChannel() {
//		return jda.getTextChannelById(getDoc().getString("introID"));
//		//TODO check if this gives an error
//	}
	
	public String getName() {
		if (getGuild() == null) {
			return this.getDoc().getString("name");
		} else {
			return getGuild().getName();
		}
	}
	public OffsetDateTime getCreationTime() {
		return OffsetDateTime.parse(this.getDoc().getString("creationtime"));
	}
	
	/*************
	 *  SETTERS  * Setters assume what you're doing is correct.
	 *  		 * Setters update straight to DB.
	 *************/
	public void setName() {
		this.setKey("name", this.getGuild().getName());
	}
	public void setOwner() {
		this.setKey("ownerid", this.getGuild().getOwner().getUser().getId());
	}
	public void updateCounts() {
		this.bsonUpdates(set("textchannels", this.getGuild().getTextChannels().size()),
						set("voicechannels", this.getGuild().getVoiceChannels().size()),
						set("members", this.getGuild().getMembers().size()));
	}
//	public void setEconomy(EcoInfo ei) {
//		this.setKey("economy", new BasicDBObject("MajSing", ei.getMaj(0))
//				.append("MajMult", ei.getMaj(1))
//				.append("MinSing", ei.getMin(0))
//				.append("MinMult", ei.getMin(1))
//				.append("startbal", new BsonDouble(ei.getStartVal()))
//				.append("dailybal", new BsonDouble(ei.getDaily())));
//	}
	
//	public void setIntroChannel(TextChannel intro) {
//		this.setKey("introID", intro.getId());
//	}
	
	@Override
	protected boolean update() {
		if (getGuild() == null) return false;
		Document gd = this.getDoc();
		Guild g = getGuild();
		if (!gd.getString("name").equals(g.getName())) setName();
		if (!gd.getString("owner").equals(g.getOwner().getUser().getId())) {
			setOwner();
			new MongoUser(g.getOwner().getUser());
		}
		if (gd.getInteger("textchannels")!=g.getTextChannels().size()
				|| gd.getInteger("voicechannels")!=g.getVoiceChannels().size()
				|| gd.getInteger("members")!=g.getMembers().size()) {
			updateCounts();
		}
		return true;
	}
	
	public static boolean exists(String guildId) {
		return exists("guilds", guildId);
	}
}
