package wh1spr.bot.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.bot.command.Command;
import wh1spr.bot.morty.Permission;

public class ChangeNameCommand extends Command {

	public ChangeNameCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker.getUser(), true)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() > 0) {
			guild.getController().setNickname(guild.getMember(jda.getSelfUser()), message.getContentStripped().split(" ",2)[1]).queue();
		} else {
			guild.getController().setNickname(guild.getMember(jda.getSelfUser()), null).queue();
		}
		
		message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
	}
}
