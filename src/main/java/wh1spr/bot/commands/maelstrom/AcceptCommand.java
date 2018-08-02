package wh1spr.bot.commands.maelstrom;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;
import wh1spr.bot.command.Command;
import wh1spr.bot.database.Database2;
import wh1spr.bot.database.modules.MaelstromModule;
import wh1spr.bot.dummy.Perm;
import wh1spr.bot.morty.MRoles;

public class AcceptCommand extends Command {

	public AcceptCommand(String name, String... aliases) {
		super(name, aliases);
	}
	
	private static final String welcomeId = "433718059254153216";
	private static MaelstromModule mael = null;

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (Perm.has(Perm.TRIAL, invoker)) return; // no need for this command
		if (!channel.getId().equals("433702216856240138")) return; // wrong channel and/or guild
		
		if (mael == null) mael = Database2.getMaelstrom();
		if (!mael.isReady()) {
			warning(message);
			return;
		}
		
		if (mael.accepted(invoker)) {
			message.delete().queue();
			return;
		}
		
		GuildController g = guild.getController();
		
		g.removeSingleRoleFromMember(guild.getMember(invoker), guild.getRoleById(MRoles.ACCEPT)).queue();
		g.addSingleRoleToMember(guild.getMember(invoker), guild.getRoleById(MRoles.GUEST)).queue();
		
		mael.setAccept(invoker, true);
		guild.getTextChannelById(welcomeId).sendMessage("*Please welcome " + invoker.getAsMention() + " to Maelstrom!*").queue();
		
		message.delete().queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		return; // nothing needs to be done
	}

}
