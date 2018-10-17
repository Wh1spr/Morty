package wh1spr.bot;

import wh1spr.bot.mongodb.Mongo;
import wh1spr.bot.mongodb.MongoUser;

public class MainTest {

	public static void main(String[] args) {
		Mongo.start();
		
		MongoUser u = new MongoUser("");
		
		
	}
}
