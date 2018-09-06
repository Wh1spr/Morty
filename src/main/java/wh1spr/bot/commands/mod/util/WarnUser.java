package wh1spr.bot.commands.mod.util;

import static com.mongodb.client.model.Updates.set;

import org.bson.BsonArray;

import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.MongoUser;

public class WarnUser extends MongoUser {

	public WarnUser(User user) {
		super(user);
		
		if (!getDoc().containsKey("warnings")) {
			this.bsonUpdates(set("warnings", new BsonArray()));
		}
	}
	
	
	
	
	
	

}
