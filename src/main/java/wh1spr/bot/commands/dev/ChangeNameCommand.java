package wh1spr.bot.commands.dev;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Perm;

public class ChangeNameCommand extends Command {

	public ChangeNameCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.hasSpec(Perm.OWNER, invoker)) {
			return;
		}
		
		if (args.size() > 0) {
			guild.getController().setNickname(guild.getMember(jda.getSelfUser()), message.getContentStripped().split(" ",2)[1]).queue();
		} else {
			guild.getController().setNickname(guild.getMember(jda.getSelfUser()), null).queue();
		}
		
		success(message);
	}
}
