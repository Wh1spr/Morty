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

public class KickUser extends MongoUser {

	public KickUser(String userId) {
		super(userId);
		
		if (!getDoc().containsKey("warnings")) {
			this.bsonUpdates(set("warnings", new BsonArray()));
		}
	}
	
	public KickUser(User user) {
		this(user.getId());
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
		MongoBot bot = new MongoBot(Main.getBot());
		bot.setKickHex(bot.getKickHex()+1);
		Kick kick = new Kick(bot.getKickHexString(), g, this.getUser(), by, reason);
		
		return kick;
	}
}
