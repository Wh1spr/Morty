package wh1spr.bot.database;

import net.dv8tion.jda.core.entities.Guild;
import wh1spr.bot.Main;
import wh1spr.bot.Tools;
//import wh1spr.bot.mongodb.Mongo;
//import wh1spr.bot.mongodb.MongoGuild;

public class EcoInfo {

	public EcoInfo(String guildId, String majSing, String majMult, String minSing, String minMult, Double start, Double daily) {
		this.guildId = guildId;
		this.majSing = majSing;
		this.majMult = majMult;
		this.minSing = minSing;
		this.minMult = minMult;
		this.start = start;
		this.daily = daily;
//		this.mongo = Mongo.getMongoGuild(getGuild());
	}
	public EcoInfo(Guild guild, String majSing, String majMult, String minSing, String minMult, Double start, Double daily) {
		this(guild.getId(), majSing, majMult, minSing, minMult, start, daily);
	}
	
//	private MongoGuild mongo = null;
	private String guildId = null;
	private String majSing = null;
	private String majMult = null;
	private String minSing = null;
	private String minMult = null;
	private Double start = null;
	private Double daily = null;
	
	public Guild getGuild() {
		return Main.getBot().getJDA().getGuildById(this.guildId);
	}
	public String[] getMaj() {
		return new String[] {majSing, majMult};
	}
	public String getMaj(int index) {
		return index<0||index>1?null:getMaj()[index];
	}
	public String[] getMin() {
		return new String[] {minSing, minMult};
	}
	public String getMin(int index) {
		return index<0||index>1?null:getMin()[index];
	}
	public Double getStartVal() {
		return this.start;
	}
	public Double getDaily() {
		return this.daily;
	}
	
	// Setters assume what you're doing is correct.
	public void setMajSing(String s) {
		this.majSing = s;
//		mongo.setEconomy(this);
	}
	public void setMajMult(String s) {
		this.majMult = s;
//		mongo.setEconomy(this);
	}
	public void setMinSing(String s) {
		this.minSing = s;
//		mongo.setEconomy(this);
	}
	public void setMinMult(String s) {
		this.minMult = s;
//		mongo.setEconomy(this);
	}
	public void setStartval(Double val) {
		this.start = Tools.round(val);
//		mongo.setEconomy(this);
	}
	public void setDailyval(Double val) {
		this.daily = Tools.round(val);
//		mongo.setEconomy(this);
	}
	
	
}
