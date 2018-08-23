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
