package wh1spr.bot.database;

import net.dv8tion.jda.core.entities.Guild;
import wh1spr.bot.Main;

public class EcoInfo {

	public EcoInfo(String guildId, String majSing, String majMult, String minSing, String minMult, Double start, Double daily) {
		this.guildId = guildId;
		this.majSing = majSing;
		this.majMult = majMult;
		this.minSing = minSing;
		this.minMult = minMult;
		this.start = start;
		this.daily = daily;
	}
	public EcoInfo(Guild guild, String majSing, String majMult, String minSing, String minMult, Double start, Double daily) {
		this(guild.getId(), majSing, majMult, minSing, minMult, start, daily);
	}
	
	private String guildId = null;
	private String majSing = null;
	private String majMult = null;
	private String minSing = null;
	private String minMult = null;
	private Double start = null;
	private Double daily = null;
	
	public Guild getGuild() {
		return Main.getBots().get(0).getJDA().getGuildById(this.guildId);
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
}
