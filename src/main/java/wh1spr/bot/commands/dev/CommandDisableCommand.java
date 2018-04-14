package wh1spr.bot.commands.dev;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.command.CommandRegistry;
import wh1spr.bot.morty.Permission;

// add in non static reference as bot object
public class CommandDisableCommand extends Command {

	public CommandDisableCommand(String name, CommandRegistry registry, String... aliases) {
		super(name, aliases);
		this.registry = registry;
	}

	private CommandRegistry registry = null;
	
	//admins can dcmd in case of abuse, overuse or unnecessary use
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.ADMIN, invoker, false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() > 0) {
			for (String cmd : args) {
				if (cmd.equals("shutdown")) {
					channel.sendMessage("`shutdown` can not be disabled.");
					continue;
				} else if (cmd.equals("eval") && !Permission.hasPerm(Permission.OWNER, invoker, false)) {
					continue;
				}
				registry.removeCommand(cmd);
			}
		}
		message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
		
	}

}
