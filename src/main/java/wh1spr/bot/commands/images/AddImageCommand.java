package wh1spr.bot.commands.images;

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

//Images gonna be individual per guild, with all images still on imgur for safety of the database
public class AddImageCommand extends Command {

	public AddImageCommand(String name, ImageRegistry registry, String... aliases) {
		super(name, aliases);
		this.registry = registry;
	}
	
	private ImageRegistry registry = null;

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.ADMIN, invoker)) {return;}
		
		if (args.size() != 2) {
			failure(message);
		} else {
			if (!args.get(1).contains("https://i.imgur.com/")) {
				failure(message);
				channel.sendMessage("The image has to be uploaded on Imgur.").queue();
				return;
			}
			if (args.get(1).length() > 35) {
				failure(message);
				channel.sendMessage("An Imgur link should be smaller than 35 characters.").queue();
				return;
			}
			if (args.get(0).length() > 20) {
				channel.sendMessage("Maximum name lenght for a command is 20 characters.").queue();
				failure(message);
				return;
			}
			
			if (Database.putImage(args.get(1), args.get(0))) {
				success(message);
				registry.registerCommand(new SendImageCommand(args.get(1), args.get(0)));
			} else {
				success(message);
				if (Database.existsImage(args.get(0))) {
					channel.sendMessage("Another image with this name already exists.").queue();
				} else {
					channel.sendMessage("Something went wrong, please mention an Owner.").queue();
				}
			}
		}
	}
}
