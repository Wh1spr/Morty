package wh1spr.morty.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class CleanCommand extends Command {

	public CleanCommand(String name, String... aliases) {
		super(name, aliases);
		this.commandInfo = new CommandInfo(this.name, "Cleans a channel's messages by user, or by number of messages, up to 100 messages.", 
				"PREFIXclean <int> \nPREFIXclean @user1 @user2... \nPREFIXclean <int> @user1 @user2...", this.aliases);
	}

	// This code has been copied from another bot of mine that I made a while back, that's why it's so messy
	// I'll update it someday, that day I'll also correctly implement it so the command itself gets removed as well
	
	//Possibilities for this command are:
	// .clean <int> 					Removes <int> past messages.
	// .clean @user1 @user2...			Removes messages by these users in the past 99 messages. >> WILL BE REMOVED
	// .clean <int> @user1 @user2...	Removes messages by these users int he past <int> messages.
	
	// I'm making it so that, if the toDelete is <= 100, I remove them 1 by 1 if an error is thrown.
	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		List<Message> messages = new ArrayList<Message>();
		Set<Message> toDelete = new HashSet<Message>();
		int toDeleteSize = 0;
		
		try {
			//get number
			int nr = 0;
			try {
				nr = Integer.valueOf(args.get(0));
			} catch (Exception e) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				return;
			}
			nr++; //for the clean message itself
			
			if (nr == 1) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				return;
			}
			
			String lastId = channel.getHistory().retrievePast(1).complete(true).get(0).getId();
			
			for (int i = nr; i > 0; i -= 100) {
				if (i % 50 > 0) {
					messages.addAll(channel.getHistoryAround(lastId, 100).complete(true).retrievePast(50).complete(true));
					lastId = messages.get(messages.size() - 1).getId();
				} else {
					messages.addAll(channel.getHistoryAround(lastId, 100).complete(true).retrievePast(i).complete(true));
				}
			}
			
			if (!message.getMentionedUsers().isEmpty()) {
				for (User user : message.getMentionedUsers()) {
					for (Message msg : messages) {
						if (msg.getAuthor() == user) toDelete.add(msg);
					}
				}
				toDelete.add(message);
			} else {
				toDelete.addAll(messages);
				toDelete.add(message);
			}
			
			//DELETE MESSAGES
			toDeleteSize = toDelete.size();
			
			if (toDelete.size() <= 100) {
				channel.deleteMessages(toDelete).queue();
			} else {
				Set<Message> nextDelete = new HashSet<Message>();
				Iterator<Message> toDeleteIter = toDelete.iterator();
				
				while (toDeleteIter.hasNext()) {
					nextDelete.add(toDeleteIter.next());
					if (nextDelete.size() == 100 || !toDeleteIter.hasNext()) {
						channel.deleteMessages(nextDelete).queue();
						toDelete.removeAll(nextDelete);
						nextDelete.clear();
					}
				}
			}
			
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			
		} catch (Exception e) {
			//when a message is older than 2 weeks
			//so now either deleting each message seperately or just stopping
			if (toDelete.size() > 100) {
				channel.sendMessage("Removed " + (toDeleteSize - toDelete.size()) + " messages. " + toDelete.size() + " messages still remain. It is not recommended to bulk delete these.").queue();
				message.addReaction(EmojiManager.getForAlias("warning").getUnicode()).queue();
			} else {
				Iterator<Message> toDeleteIter = toDelete.iterator();
				while(toDeleteIter.hasNext()) {
					channel.deleteMessageById(toDeleteIter.next().getId()).queue();
				}
				message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			}
			
		}
	}

}
