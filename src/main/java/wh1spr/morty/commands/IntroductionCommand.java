package wh1spr.morty.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.morty.C;
import wh1spr.morty.Database;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;
// TO BE REDONE
public class IntroductionCommand extends Command {

	public IntroductionCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.MEMBER, invoker.getUser(), false) || !guild.getId().equals(C.GUILD)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (!guild.getId().equals(C.GUILD)) return;
		
		if (message.getMentionedUsers().size() == 1) {
			User target = message.getMentionedUsers().get(0);
			String msgId = Database.getIntroductionId(target);
			if (msgId.equals("0")) {
				channel.sendMessage(":x: This user does not have an introduction.").queue();
			} else {
				Message targetMsg = jda.getTextChannelById(C.CHANNEL_INTRODUCTION).getMessageById(msgId).complete();
				channel.sendMessage(":white_check_mark: **Introduction for " + target.getName() + ":** \n\n" + targetMsg.getRawContent()).queue();
			}
		}
	}

}
