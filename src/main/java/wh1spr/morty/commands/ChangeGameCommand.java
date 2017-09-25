package wh1spr.morty.commands;

import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class ChangeGameCommand extends Command {

	public ChangeGameCommand(String name, String... aliases) {
		super(name, aliases);
		this.commandInfo = new CommandInfo(this.name, "Changes Morty's status message.", "PREFIXchangegame [message]", this.aliases);
	}

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker.getUser(), true)) {
			channel.deleteMessageById(message.getId()).queue();
			return;
		}
		
		if (args.size() > 0) {
			jda.getPresence().setGame(Game.of(message.getStrippedContent().split(" ",3)[2]));
		} else {
			jda.getPresence().setGame(null);
		}
		
	}

}
