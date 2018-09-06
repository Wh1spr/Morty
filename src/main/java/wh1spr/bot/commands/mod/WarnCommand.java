package wh1spr.bot.commands.mod;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.mod.util.Warning;
import wh1spr.bot.dummy.Perm;
import wh1spr.bot.mongodb.MongoBot;

public class WarnCommand extends Command {

	public WarnCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.STAFF, invoker)) return;
		
		if (message.getMentionedUsers().isEmpty()) {
			failure(message);
			return;
		}
		Member toWarn = message.getMentionedMembers(guild).get(0);
		String reason = message.getContentDisplay().split(" ", 3)[2];
		
		// Role Check.
		if (!guild.getMember(invoker).canInteract(toWarn)) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: This person has a higher rank than you. You cannot warn them.").build()).queue();
			return;
		}
		
		// Here I'm sure I am allowed to warn a user. 
		MongoBot bot = new MongoBot(Main.getBot());
		bot.setWarnHex(bot.getWarnHex()+1);
		Warning warn = new Warning(bot.getWarnHexString(), guild, toWarn.getUser(), invoker, reason);
		
		MessageEmbed e = new EmbedBuilder().setColor(Color.orange).setTitle(":warning: You have been warned!")
				.setDescription(String.format("By **%s**%nReason: *%n*", warn.getIssuername(), warn.getReason()))
				.setTimestamp(LocalDateTime.now()).build();
		
		toWarn.getUser().openPrivateChannel().complete().sendMessage(e).complete();
		channel.sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setTitle(":warning: " + toWarn.getUser().getName() + " has been warned.")
				.setDescription("Warning ID: **" + warn.getHexString() + "**").build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}
