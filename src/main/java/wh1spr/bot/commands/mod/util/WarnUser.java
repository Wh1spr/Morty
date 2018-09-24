package wh1spr.bot.commands.mod.util;

import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonArray;

import com.mongodb.client.model.Updates;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.mongodb.MongoBot;
import wh1spr.bot.mongodb.MongoUser;

public class WarnUser extends MongoUser {
	
	public WarnUser(String userId) {
		super(userId);
		
		if (!getDoc().containsKey("warnings")) {
			this.bsonUpdates(set("warnings", new BsonArray()));
		}
	}
	
	public WarnUser(User user) {
		this(user.getId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Warning> getWarnings() {
		List<String> hexes = (List<String>) this.getDoc().get("warnings");
		List<Warning> warning = new ArrayList<Warning>();
		hexes.forEach(el->warning.add(new Warning(el)));
		return warning;
	}
	
	public void addWarning(String hex) {
		this.bsonUpdates(Updates.push("warnings", hex));
	}
	
	public Warning warn(Guild g, User by, String reason) {
		MongoBot bot = new MongoBot(Main.getBot());
		bot.setWarnHex(bot.getWarnHex()+1);
		Warning warn = new Warning(bot.getWarnHexString(), g, this.getUser(), by, reason);
		
		return warn;
	}
}
