package wh1spr.bot.commands.mod;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Perm;

public class BanCommand extends Command {

	public BanCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.ADMIN, guild.getMember(invoker))) return;
		
		Member toBan = message.getMentionedMembers(guild).get(0);
		if (toBan==null) {
			failure(message);
			return;
		}
		String reason = message.getContentDisplay().split(" ", 3)[2];
		try {
			toBan.getUser().openPrivateChannel().complete().sendMessage(String.format("You have been banned from *%s*.\n**Reason:** *%s*", guild.getName(), reason)).complete();
			guild.getController().ban(toBan, 0).complete();
		} catch(HierarchyException e) {
			failure(message);
			return;
		}
		success(message);
	}

}
