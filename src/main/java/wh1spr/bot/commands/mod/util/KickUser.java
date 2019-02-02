package wh1spr.bot.commands.mod.util;

import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonArray;

import com.mongodb.client.model.Updates;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.mongodb.MongoUser;

public class KickUser extends MongoUser {
	
	public KickUser(Long userId) {
		super(userId);
		
		if (!getDoc().containsKey("kicks")) {
			this.bsonUpdates(set("kicks", new BsonArray()));
		}
	}
	
	public KickUser(User user) {
		this(user.getIdLong());
	}
	
	@SuppressWarnings("unchecked")
	public List<Kick> getKicks() {
		List<String> hexes = (List<String>) this.getDoc().get("kicks");
		List<Kick> kicks = new ArrayList<Kick>();
		hexes.forEach(el->kicks.add(new Kick(el)));
		return kicks;
	}
	
	public void addKick(String hex) {
		this.bsonUpdates(Updates.push("kicks", hex));
	}
	
	public Kick kick(Guild g, User by, String reason) {
		return new Kick(Kick.getAndSetNextHex(), g, this.getUser(), by, reason);
	}
}
