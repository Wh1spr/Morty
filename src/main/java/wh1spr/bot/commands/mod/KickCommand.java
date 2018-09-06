package wh1spr.bot.commands.mod;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.mod.util.Kick;
import wh1spr.bot.mongodb.MongoBot;

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
		User kicked = toKick.getUser();
		String reason = message.getContentDisplay().split(" ", 3)[2];
		
		// Role Check
		Role invokerRole = guild.getMember(invoker).getRoles().isEmpty()?guild.getPublicRole():guild.getMember(invoker).getRoles().get(0);
		Role toKickRole = toKick.getRoles().isEmpty()?guild.getPublicRole():toKick.getRoles().get(0);
		Role botRole = guild.getSelfMember().getRoles().get(0); // Bots always have their own role
		if (invokerRole.getPosition() <= toKickRole.getPosition() || toKick.isOwner()) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You can't kick someone that has a role higher than yours.").build()).queue();
			return;
		} else if (botRole.getPosition() <= toKickRole.getPosition()) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: I can't kick someone with a higher rank than myself!")
					.setDescription("To fix this, place the Morty role higher in the role list.").build()).queue();
			return;
		}
		
		// Here I'm sure I can kick a user. 
		
		MongoBot bot = new MongoBot(Main.getBot());
		bot.setKickHex(bot.getKickHex()+1);
		Kick kick = new Kick(bot.getKickHexString(), guild, kicked, invoker, reason);
		
		toKick.getUser().openPrivateChannel().complete().sendMessage(String.format("You have been kicked from *%s*.\n**Reason:** *%s*", guild.getName(), reason)).complete();
		channel.sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setTitle(toKick.getUser().getName() + " has been kicked from the server.")
				.setDescription("Ban ID: " + kick.getHexString()).build()).queue();
		
		guild.getController().kick(toKick, reason).complete();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}