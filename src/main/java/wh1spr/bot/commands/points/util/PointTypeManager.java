package wh1spr.bot.commands.points.util;

import wh1spr.bot.mongodb.MongoUser;

public class PointTypeManager {

	public PointTypeManager(String moduleName, String typeName) {
		if (moduleName == null) throw new IllegalArgumentException("moduleName cannot be null.");
		if (typeName == null) throw new IllegalArgumentException("typeName cannot be null.");
		
		this.moduleName = moduleName;
		this.typeName = typeName;
	}
	
	private String moduleName = null;
	private String typeName = null;
	
	public String getModuleName() {
		return this.moduleName;
	}
	public String getTypeName() {
		return this.typeName;
	}
	
	public PointsUser getPointsUser(MongoUser mongo) {
		return new PointsUser(mongo, getModuleName(), getTypeName());
	}
	
}
