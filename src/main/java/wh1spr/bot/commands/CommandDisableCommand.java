package wh1spr.bot.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.bot.command.Command;
import wh1spr.bot.morty.Morty;
import wh1spr.bot.morty.Permission;

// add in non static reference as bot object
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
				if (cmd.equals("shutdown")) {
					channel.sendMessage("`shutdown` can not be disabled.");
					continue;
				} else if (cmd.equals("eval") && !Permission.hasPerm(Permission.OWNER, invoker.getUser(), false)) {
					continue;
				}
				Morty.commandRegistry.removeCommand(cmd);
			}
		}
		message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
		
	}

}
