package wh1spr.bot.commands.points;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;
import wh1spr.bot.commands.points.util.PointsUser;
import wh1spr.bot.mongodb.MongoDB;

public class PointBalanceCommand extends PointsCommand {

	public PointBalanceCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check needed
		PointTypeManager tm = this.getTypeManager();
		
		PointsUser u = tm.getPointsUser(MongoDB.getMongoUser(invoker));
		
		EmbedBuilder e = new EmbedBuilder().setColor(Color.GREEN);
		e.setTitle(String.format("You have %d Event Point" + (u.getPoints()==1?"":"s"), u.getPoints()));
		
		channel.sendMessage(e.build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		this.cantUse(channel);
	}

}
