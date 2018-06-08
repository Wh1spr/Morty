package wh1spr.bot.commands.dev;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Perm;

public class SendFromJDACommand extends Command {

	public SendFromJDACommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.hasSpec(Perm.OWNER, invoker)) {return;}
		
		if (args.size() > 1) {
			MessageChannel channelTo = jda.getTextChannelById(args.get(0));
			if (channelTo == null) {failure(message);return;}
			
			channelTo.sendMessage(message.getContentRaw().split(" ", 2)[1]).queue();
			success(message);
			return;
		} else {
			failure(message);
		}
	}
}