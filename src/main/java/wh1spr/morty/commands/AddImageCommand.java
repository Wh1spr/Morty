package wh1spr.morty.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.morty.Database;
import wh1spr.morty.Morty;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class AddImageCommand extends Command {

	public AddImageCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() != 2) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
		} else {
			if (!args.get(1).contains("imgur.com")) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				channel.sendMessage("The image has to be uploaded on Imgur.").queue();
				return;
			}
			if (args.get(0).length() > 30) {
				channel.sendMessage("Maximum name lenght for a command is 30 characters.").queue();
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				return;
			}
			
			if (Database.putImage(args.get(1), args.get(0))) {
				message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
				Morty.imageRegistry.registerCommand(new SendImageCommand(args.get(1), args.get(0)));
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
