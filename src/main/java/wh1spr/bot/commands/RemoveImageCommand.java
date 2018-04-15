package wh1spr.bot.commands;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.command.ImageRegistry;
import wh1spr.bot.dummy.Perm;
import wh1spr.bot.morty.Database;

//add in non static reference as bot object
public class RemoveImageCommand extends Command {

	public RemoveImageCommand(String name, ImageRegistry registry, String... aliases) {
		super(name, aliases);
		this.registry = registry;
	}

	private ImageRegistry registry = null;
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.ADMIN, invoker)) {return;}
		
		if (args.size() != 1) {
			failure(message);
		} else {
			if (args.get(0).length() > 35) {
				failure(message);
				channel.sendMessage("Name or Imgur URL can not be longer than 30 characters.").queue();
				return;
			}
			
			Database.removeImage(args.get(0));
			registry.removeCommand(args.get(0));
			success(message);
		}
		
	}

}
