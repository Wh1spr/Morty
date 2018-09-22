package wh1spr.bot.commands.points.util;

import wh1spr.bot.mongodb.MongoUser;

public class PointTypeManager {

	public PointTypeManager(String moduleName, String typeName, String pointNameSing, String pointNameMult) {
		if (moduleName == null) throw new IllegalArgumentException("moduleName cannot be null.");
		if (typeName == null) throw new IllegalArgumentException("typeName cannot be null.");
		
		if (pointNameSing == null) pointNameSing = "Point";
		if (pointNameMult == null) pointNameMult = "Points";
		
		this.moduleName = moduleName;
		this.typeName = typeName;
		
		this.pointNameSing = pointNameSing;
		this.pointNameMult = pointNameMult;
	}
	
	private String moduleName = null;
	private String typeName = null;
	
	private String pointNameSing = null;
	private String pointNameMult = null;
	
	public String getModuleName() {
		return this.moduleName;
	}
	public String getTypeName() {
		return this.typeName;
	}
	
	public String getPointNameSing() {
		return this.pointNameSing;
	}
	public String getPointNameMult() {
		return this.pointNameMult;
	}
	
	public PointsUser getPointsUser(MongoUser mongo) {
		return new PointsUser(mongo, getModuleName(), getTypeName());
	}
	
}
