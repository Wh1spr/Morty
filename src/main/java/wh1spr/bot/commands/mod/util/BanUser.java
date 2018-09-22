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

public class BanUser extends MongoUser {

	public BanUser(String userId) {
		super(userId);
		
		if (!getDoc().containsKey("bans")) {
			this.bsonUpdates(set("bans", new BsonArray()));
		}
	}
	
	public BanUser(User user) {
		this(user.getId());
	}
	
	@SuppressWarnings("unchecked")
	public List<Ban> getBans() {
		List<String> hexes = (List<String>) this.getDoc().get("kicks");
		List<Ban> bans = new ArrayList<Ban>();
		hexes.forEach(el->bans.add(new Ban(el)));
		return bans;
	}
	
	public void addBan(String hex) {
		this.bsonUpdates(Updates.push("bans", hex));
	}
	
	public Ban ban(Guild g, User by, String reason) {
		MongoBot bot = new MongoBot(Main.getBot());
		bot.setBanHex(bot.getBanHex()+1);
		Ban ban = new Ban(bot.getWarnHexString(), g, this.getUser(), by, reason);
		
		return ban;
	}
}
