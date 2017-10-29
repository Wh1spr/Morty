package wh1spr.morty.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.morty.Morty;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class CommandDisableCommand extends Command {

	public CommandDisableCommand(String name, String... aliases) {
		super(name, aliases);
	}

	//admins can dcmd in case of abuse, overuse or unnecessary use
	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() > 0) {
			for (String cmd : args) {
				Morty.commandRegistry.removeCommand(cmd);
			}
		}
		message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
		
	}

}
