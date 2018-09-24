package wh1spr.bot.commands.mod;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.mod.util.Ban;
import wh1spr.bot.commands.mod.util.BanUser;

public class BanCommand extends Command {

	public BanCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!guild.getMember(invoker).hasPermission(Permission.BAN_MEMBERS)) return;
		
		if (message.getMentionedUsers().isEmpty()) {
			failure(message);
			return;
		}
		Member toBan = message.getMentionedMembers(guild).get(0);
		String reason = message.getContentDisplay().split(" ", 3)[2];
		
		// Role Check.
		if (!guild.getMember(invoker).canInteract(toBan) || !guild.getSelfMember().canInteract(toBan)) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You are not able to ban this user.").build()).queue();
			return;
		}
		
		// Here I'm sure I can ban a user.
		BanUser bu =  new BanUser(toBan.getUser());
		Ban ban = bu.ban(guild, invoker, reason);
		
		MessageEmbed e = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You have been banned!")
				.setDescription(String.format("By **%s**%nFrom **%s**%nReason: *%s*", ban.getIssuername(), guild.getName(), ban.getReason()))
				.setTimestamp(LocalDateTime.now()).build();
		
		toBan.getUser().openPrivateChannel().complete().sendMessage(e).complete();
		channel.sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setTitle(toBan.getUser().getName() + " has been banned from the server.")
				.setDescription("Ban ID: **" + ban.getHexString() + "**").build()).queue();
		
		guild.getController().ban(toBan, 0, reason).complete();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}
