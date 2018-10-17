package wh1spr.bot.mongodb;

import org.bson.BsonInt32;
import org.bson.Document;

import com.mongodb.client.MongoDatabase;

import net.dv8tion.jda.core.entities.Guild;

class MongoCreator {

	private MongoDatabase db = null;
	
	MongoCreator(MongoDatabase db) {
		this.db = db;
	}
	
	
	MongoGuild createGuild(Guild guild) {
		if (MongoGuild.exists(guild.getId())) throw new IllegalArgumentException("Cannot create a guild that already exists in db.");
		
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
