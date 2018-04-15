package wh1spr.bot.commands;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Perm;
import wh1spr.bot.morty.C;
import wh1spr.bot.morty.Database;
// TO BE REDONE
@Deprecated
public class IntroductionCommand extends Command {

	public IntroductionCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.MEMBER, invoker)) return;
		
		if (!guild.getId().equals(C.GUILD)) return;
		
		if (message.getMentionedUsers().size() == 1) {
			User target = message.getMentionedUsers().get(0);
			String msgId = Database.getIntroductionId(target);
			if (msgId.equals("0")) {
				channel.sendMessage(":x: This user does not have an introduction.").queue();
			} else {
				Message targetMsg = jda.getTextChannelById(C.CHANNEL_INTRODUCTION).getMessageById(msgId).complete();
				channel.sendMessage(":white_check_mark: **Introduction for " + target.getName() + ":** \n\n" + targetMsg.getContentRaw()).queue();
			}
		}
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {}

}
