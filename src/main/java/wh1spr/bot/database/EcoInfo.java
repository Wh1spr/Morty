package wh1spr.bot.database;

public class EcoInfo {

	public EcoInfo(String majSing, String majMult, String minSing, String minMult, Double start, Double daily, Double min) {
		this.majSing = majSing;
		this.majMult = majMult;
		this.minSing = minSing;
		this.minMult = minMult;
		this.start = start;
		this.daily = daily;
	}
	
	private String majSing = null;
	private String majMult = null;
	private String minSing = null;
	private String minMult = null;
	private Double start = null;
	private Double daily = null;
}
