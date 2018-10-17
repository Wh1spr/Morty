package wh1spr.bot.mongodb;

import java.util.List;

import org.bson.BsonInt32;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.dummy.Bot;

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
	
	MongoBot createBot(Bot b) {
		if (MongoBot.exists(b.getJDA().getSelfUser().getId())) throw new IllegalArgumentException("Cannot create a bot that already exists in db.");
		if (!MongoUser.exists(b.getJDA().getSelfUser().getId())) createUser(b.getJDA().getSelfUser());
		
		Document bot = new Document("_id", b.getJDA().getSelfUser().getId());
		bot.append("name", b.getJDA().getSelfUser().getName());
		bot.append("guilds", b.getJDA().getGuilds().size())
		   .append("users", b.getJDA().getUsers().size());
		db.getCollection("bots").insertOne(bot);
		
		return new MongoBot(b);
	}
	
}
