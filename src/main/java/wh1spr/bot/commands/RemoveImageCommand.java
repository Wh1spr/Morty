package wh1spr.bot.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import wh1spr.bot.command.Command;
import wh1spr.bot.command.ImageRegistry;
import wh1spr.bot.morty.Database;
import wh1spr.bot.morty.Permission;

//add in non static reference as bot object
public class RemoveImageCommand extends Command {

	public RemoveImageCommand(String name, ImageRegistry registry, String... aliases) {
		super(name, aliases);
		this.registry = registry;
	}

	private ImageRegistry registry = null;
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() != 1) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
		} else {
			if (args.get(0).length() > 35) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				channel.sendMessage("Name or Imgur URL can not be longer than 30 characters.").queue();
				return;
			}
			
			Database.removeImage(args.get(0));
			registry.removeCommand(args.get(0));
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
		}
		
	}

}
