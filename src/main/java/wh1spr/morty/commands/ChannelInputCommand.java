package wh1spr.morty.commands;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import wh1spr.morty.C;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class ChannelInputCommand extends Command {

	public ChannelInputCommand(String name, String channelId, String... aliases) {
		super(name, aliases);
		this.channelId = channelId;
	}
	
	private final String channelId;

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		//only to be used in Informatica
		if (!guild.getId().equals(C.GUILD)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		List<Message> channelMsg = null;
		try {
			channelMsg = guild.getTextChannelById(channelId).getHistory().retrievePast(100).complete(true);
		} catch (RateLimitedException e) {} // never happens, since we use true
		
		Collections.reverse(channelMsg);
		
		Iterator<Message> iter = channelMsg.iterator();
		String privateMsg = "";
		
		while (iter.hasNext()) {
			String next = iter.next().getContent();
			
			if (next.startsWith(String.valueOf(Permission.GUEST)) && Permission.hasPerm(Permission.GUEST, invoker.getUser(), false)) {
				next = next.replaceFirst("1", "");
				privateMsg += next + "@@@\n";
			} else if (next.startsWith(String.valueOf(Permission.MEMBER)) && Permission.hasPerm(Permission.MEMBER, invoker.getUser(), false)) {
				next = next.replaceFirst("2", "");
				privateMsg += next + "@@@\n";
			} else if (next.startsWith(String.valueOf(Permission.GRADUATED)) && Permission.hasPerm(Permission.GRADUATED, invoker.getUser(), false)) {
				next = next.replaceFirst("3", "");
				privateMsg += next + "@@@\n";
			} else if (next.startsWith(String.valueOf(Permission.WINA)) && Permission.hasPerm(Permission.WINA, invoker.getUser(), false)) {
				next = next.replaceFirst("5", "");
				privateMsg += next + "@@@\n";
			} else if (next.startsWith(String.valueOf(Permission.ADMIN)) && Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
				next = next.replaceFirst("7", "");
				privateMsg += next + "@@@\n";
			} else if (next.startsWith(String.valueOf(Permission.OWNER)) && Permission.hasPerm(Permission.OWNER, invoker.getUser(), false)) {
				next = next.replaceFirst("9", "");
				privateMsg += next + "@@@\n";
			}
		}
		
		if (privateMsg.length() == 0) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		if (privateMsg.length() <= 2000) {
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			invoker.getUser().openPrivateChannel().complete().sendMessage(privateMsg.replaceAll("@@@", "")).queue();
		} else {
			// THIS HAS NOT BEEN TESTED YET
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			PrivateChannel pChan = invoker.getUser().openPrivateChannel().complete();
			String nextMsg = "";
			String nextPart = "";
			
			while (privateMsg.length() > 2) { //just in case, should be 1 anyways
				nextPart = privateMsg.substring(0, privateMsg.indexOf("@@@"));
				if (nextMsg.length() + nextPart.length() < 1990) {
					nextMsg += "\n" + nextPart;
					privateMsg = privateMsg.substring(privateMsg.indexOf("@@@") + 3);
				} else {
					pChan.sendMessage(nextMsg).queue();
					nextMsg = "";
				}
			}
			if (nextMsg.length() > 2) {
				pChan.sendMessage(".\n" + nextMsg).queue();
			}
		}
	}
}
