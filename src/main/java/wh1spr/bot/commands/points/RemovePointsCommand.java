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
import wh1spr.bot.Main;
import wh1spr.bot.Tools;
import wh1spr.bot.commands.mod.util.WarnUser;
import wh1spr.bot.commands.mod.util.Warning;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;
import wh1spr.bot.commands.points.util.PointsUser;
import wh1spr.bot.dummy.Perm;
import wh1spr.bot.mongodb.Mongo;

public class RemovePointsCommand extends PointsCommand {

	public RemovePointsCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!RemovePoints <@user> <amount> <reason>`").build();
	private static EmbedBuilder success = new EmbedBuilder().setColor(Color.ORANGE).setTitle(":white_check_mark: Action succesful.");
	

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check needed
		if (!Perm.has(Perm.OWNER, invoker)) return;
		PointTypeManager tm = this.getTypeManager();
		
		if (args.size() < 2) {
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
		String reason = (message.getContentRaw() + " ").split(" ", 4)[3];
		
		WarnUser wu = new WarnUser(message.getMentionedUsers().get(0));
		Warning w = wu.warn(guild, invoker, String.valueOf(amount) + " points removed: " + reason);
		
		u.setPoints((u.getPoints() - amount)<0?0:(u.getPoints() - amount));
		
		channel.sendMessage(success.setDescription(String.format("**%d EventPoints** have been removed from **%s**'s balance (%d).", amount, u.getMongoUser().getUser().getName(), u.getPoints()))
				.appendDescription(reason.equals("")?"":("\nReason: " + reason))
				.appendDescription(String.format("%nWarning hex: **%s**", w.getHexString()))
				.build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		this.cantUse(channel);
	}

}