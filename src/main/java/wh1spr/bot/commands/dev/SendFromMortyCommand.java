package wh1spr.bot.commands.dev;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.morty.Permission;

public class SendFromMortyCommand extends Command {

	public SendFromMortyCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker, true)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() > 1) {
			MessageChannel channelTo = jda.getTextChannelById(args.get(0));
			if (channelTo == null) {message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();return;}
			
			Bot morty = Main.getBot("MORTY");
			if (morty == null) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				return;
			}
			
			channelTo.sendMessage(message.getContentRaw().replaceFirst(morty.getPrefix() + this.name, "").replaceFirst(args.get(0), "").trim()).queue();
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			return;
		} else {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
		}
	}
}