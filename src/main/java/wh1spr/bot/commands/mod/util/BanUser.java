package wh1spr.bot.commands.mod.util;

import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonArray;

import com.mongodb.client.model.Updates;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.MongoUser;

public class BanUser extends MongoUser {
	
	public BanUser(Long userId) {
		super(userId);
		
		if (!getDoc().containsKey("bans")) {
			this.bsonUpdates(set("bans", new BsonArray()));
		}
	}
	
	public BanUser(User user) {
		this(user.getIdLong());
	}
	
	@SuppressWarnings("unchecked")
	public List<Ban> getBans() {
		List<String> hexes = (List<String>) this.getDoc().get("bans");
		List<Ban> bans = new ArrayList<Ban>();
		hexes.forEach(el->bans.add(new Ban(el)));
		return bans;
	}
	
	public void addBan(String hex) {
		this.bsonUpdates(Updates.push("bans", hex));
	}
	
	public Ban ban(Guild g, User by, String reason) {
		return new Ban(Ban.getAndSetNextHex(), g, this.getUser(), by, reason);
	}
}
