package wh1spr.bot.commands.points;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;
import wh1spr.bot.commands.points.util.PointsUser;
import wh1spr.bot.mongodb.Mongo;

public class PointBalanceCommand extends PointsCommand {

	public PointBalanceCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}

	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!Balance [@user]`").build();
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check needed
		PointTypeManager tm = this.getTypeManager();
		
		PointsUser u = null;
		EmbedBuilder e = new EmbedBuilder().setColor(Color.GREEN);
		
		if (args.isEmpty()) {
			u = tm.getPointsUser(Mongo.getMongoUser(invoker));
			e.setTitle(String.format("You have **%d EventPoint" + (u.getPoints()==1?"**":"s**"), u.getPoints()));
		} else if (message.getMentionedMembers().size() == 1 && args.size()==1) {
			u = tm.getPointsUser(Mongo.getMongoUser(message.getMentionedUsers().get(0)));
			e.setTitle(String.format("%s has **%d EventPoint" + (u.getPoints()==1?"**":"s**"), u.getMongoUser().getUserMention(), u.getPoints()));
		} else {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		channel.sendMessage(e.build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		this.cantUse(channel);
	}

}
