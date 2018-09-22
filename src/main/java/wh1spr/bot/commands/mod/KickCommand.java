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
import wh1spr.bot.commands.mod.util.Kick;
import wh1spr.bot.commands.mod.util.KickUser;

public class KickCommand extends Command {

	public KickCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!guild.getMember(invoker).hasPermission(Permission.KICK_MEMBERS)) return;
		
		if (message.getMentionedUsers().isEmpty()) {
			failure(message);
			return;
		}
		Member toKick = message.getMentionedMembers(guild).get(0);
		String reason = message.getContentDisplay().split(" ", 3)[2];
		
		// Role Check.
		if (!guild.getMember(invoker).canInteract(toKick) || !guild.getSelfMember().canInteract(toKick)) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You are not able to kick this user.").build()).queue();
			return;
		}
		
		// Here I'm sure I can kick a user. 
		KickUser ku =  new KickUser(toKick.getUser());
		Kick kick = ku.kick(guild, invoker, reason);
		
		MessageEmbed e = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You have been kicked!")
				.setDescription(String.format("By **%s**%nFrom **%s**%nReason: *%s*", kick.getIssuername(), guild.getName(), kick.getReason()))
				.setTimestamp(LocalDateTime.now()).build();
		
		toKick.getUser().openPrivateChannel().complete().sendMessage(e).complete();
		channel.sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setTitle(toKick.getUser().getName() + " has been kicked from the server.")
				.setDescription("Kick ID: **" + kick.getHexString() + "**").build()).queue();
		
		guild.getController().kick(toKick, reason).complete();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}