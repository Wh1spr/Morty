package wh1spr.morty.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.morty.Morty;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class SendFromMortyCommand extends Command {

	public SendFromMortyCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker.getUser(), true)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() > 1) {
			TextChannel channelTo = guild.getTextChannelById(args.get(0));
			if (channelTo == null) {message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();return;}
			channelTo.sendMessage(message.getContentRaw().replaceFirst(Morty.PREFIX + this.name, "").replaceFirst(args.get(0), "").trim()).queue();
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			return;
		} else {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
		}
	}
}