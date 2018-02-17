package wh1spr.bot.commands;

import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import wh1spr.bot.command.Command;
import wh1spr.bot.morty.Permission;

public class ChangeGameCommand extends Command {

	public ChangeGameCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker.getUser(), true)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		if (args.size() > 0) {
			jda.getPresence().setGame(Game.of(GameType.DEFAULT, message.getContentStripped().split(" ",2)[1]));
		} else {
			jda.getPresence().setGame(null);
		}
		
		message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
		
	}

}
