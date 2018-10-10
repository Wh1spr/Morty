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
import wh1spr.bot.Tools;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;
import wh1spr.bot.commands.points.util.PointsUser;
import wh1spr.bot.dummy.Perm;
import wh1spr.bot.mongodb.Mongo;

public class AddPointsCommand extends PointsCommand {

	public AddPointsCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!AddPoints <@user> <amount>`").build();
	private static EmbedBuilder success = new EmbedBuilder().setColor(Color.GREEN).setTitle(":white_check_mark: Success!");
	

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check needed
		if (!Perm.has(Perm.OWNER, invoker)) return;
		PointTypeManager tm = this.getTypeManager();
		
		if (args.size() != 2) {
			channel.sendMessage(failsyntax).queue();
			return;
		} else if (!Tools.isPosInteger(args.get(1))) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		
		if (message.getMentionedMembers().size() != 1) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		PointsUser u = tm.getPointsUser(Mongo.getMongoUser(message.getMentionedUsers().get(0)));
		int amount = Integer.parseInt(args.get(1));
		u.setPoints(u.getPoints() + amount);
		
		channel.sendMessage(success.setDescription(String.format("**%d EventPoints** have been added to **%s**'s balance.", amount, u.getMongoUser().getUser().getName())).build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		this.cantUse(channel);
	}

}
