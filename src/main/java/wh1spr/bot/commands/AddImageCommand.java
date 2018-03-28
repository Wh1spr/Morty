package wh1spr.bot.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.command.ImageRegistry;
import wh1spr.bot.morty.Database;
import wh1spr.bot.morty.Permission;

//Images gonna be individual per guild, with all images still on imgur for safety of the database
public class AddImageCommand extends Command {

	public AddImageCommand(String name, ImageRegistry registry, String... aliases) {
		//add in bot object.
		super(name, aliases);
		this.registry = registry;
		
	}
	
	private ImageRegistry registry = null;

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.ADMIN, invoker, false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		if (args.size() != 2) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
		} else {
			if (!args.get(1).contains("https://i.imgur.com/")) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				channel.sendMessage("The image has to be uploaded on Imgur.").queue();
				return;
			}
			if (args.get(1).length() > 35) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				channel.sendMessage("An Imgur link should be smaller than 35 characters.").queue();
				return;
			}
			if (args.get(0).length() > 20) {
				channel.sendMessage("Maximum name lenght for a command is 20 characters.").queue();
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				return;
			}
			
			if (Database.putImage(args.get(1), args.get(0))) {
				message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
				registry.registerCommand(new SendImageCommand(args.get(1), args.get(0)));
			} else {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				if (Database.existsImage(args.get(0))) {
					channel.sendMessage("Another image with this name already exists.").queue();
				} else {
					channel.sendMessage("Something went wrong, please mention an Owner.").queue();
				}
			}
		}
	}
}
