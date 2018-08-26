package wh1spr.bot.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonInt32;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

class MongoCreator {

	private JDA jda = null;
	private MongoDatabase db = null;
	
	MongoCreator(JDA Jda, MongoDatabase db) {
		this.jda = Jda;
		this.db = db;
	}
	
	MongoUser createUser(User user) {
		if (MongoDB.exists(user)) throw new IllegalArgumentException("Cannot create a user that already exists in db.");
		
		Document u = new Document("_id", user.getId());
		u.append("mention", user.getName() + "#" + user.getDiscriminator());
		
		List<Guild> guilds = user.getMutualGuilds();
		Document gs = new Document();
		guilds.forEach(g->{
			gs.append(g.getId(), new BasicDBObject()); //stuff like balance can be added here or on first use
		});
		u.append("guilds", gs);
		db.getCollection("users").insertOne(u);
		
		return new MongoUser(user);
	}
	
	MongoGuild createGuild(Guild guild) {
		if (MongoDB.exists(guild)) throw new IllegalArgumentException("Cannot create a guild that already exists in db.");
		
		Document g = new Document("_id", guild.getId());
		g.append("name", guild.getName())
			.append("ownerid", guild.getOwner().getUser().getId())
			.append("textchannels", new BsonInt32(guild.getTextChannels().size()))
			.append("voicechannels", new BsonInt32(guild.getVoiceChannels().size()))
			.append("members", new BsonInt32(guild.getMembers().size()));
		db.getCollection("guilds").insertOne(g);
		
		return new MongoGuild(guild);
	}
	
}
