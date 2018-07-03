package wh1spr.bot.commands.maelstrom;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Perm;
import wh1spr.bot.morty.MRoles;

public class AcceptCommand extends Command {

	public AcceptCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (Perm.has(Perm.TRIAL, invoker)) return; // no need for this command
		if (!channel.getId().equals("433702216856240138")) return; // wrong channel and/or guild
		
		GuildController g = guild.getController();
		
		g.removeSingleRoleFromMember(guild.getMember(invoker), guild.getRoleById(MRoles.ACCEPT));
		g.addSingleRoleToMember(guild.getMember(invoker), guild.getRoleById(MRoles.GUEST));
		
		message.delete().queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		return; // nothing needs to be done
	}

}
