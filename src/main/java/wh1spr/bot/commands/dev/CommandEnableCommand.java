package wh1spr.bot.commands.dev;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.command.CommandRegistry;
import wh1spr.bot.dummy.Perm;

public class CommandEnableCommand extends Command {

	public CommandEnableCommand(String name, CommandRegistry registry, String... aliases) {
		super(name, aliases);
		this.registry = registry;
		this.setMaelstromOnly(false);
	}

	private CommandRegistry registry = null;
	
	//bot owners can dcmd in case of abuse, overuse or unnecessary use
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.hasSpec(Perm.OWNER, invoker)) {
			return;
		}
		
		if (args.size() > 0) {
			for (String cmd : args) {
				Command c = registry.getCommand(cmd).command;
				if (c!=null) c.disable();
			}
		}
		success(message);
		
	}

}
