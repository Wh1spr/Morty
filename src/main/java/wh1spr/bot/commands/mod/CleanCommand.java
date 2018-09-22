package wh1spr.bot.commands.mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Perm;

public class CleanCommand extends Command {

	public CleanCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}
	
	//Possibilities for this command are:
	// .clean <int> 					Removes <int> past messages.
	// .clean <int> @user1 @user2...	Removes messages by these users int he past <int> messages.
	
	// I'm making it so that, if the toDelete is <= 100, I remove them 1 by 1 if an error is thrown.
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.STAFF, invoker)) {return;}
		
		TextChannel tchannel = (TextChannel) channel;
		
		List<Message> messages = new ArrayList<Message>();
		Set<Message> toDelete = new HashSet<Message>();
		int deleted = 0;
		int nr = 0;
		int total = 0;
		
		try {
			//get number
			try {
				nr = Integer.valueOf(args.get(0));
				if (nr < 0) nr = 0;
			} catch (Exception e) {
				failure(message);
				return;
			}
			nr++; //for the clean message itself
			
			if (nr == 1) {
				failure(message);
				return;
			}
			
			total = nr;
			
			for (int i = nr; i > 0; i -= 100) {
				messages.clear();
				toDelete.clear();
				if (i % 100 > 0 && (i / 100) >= 1) {
					messages.addAll(channel.getHistory().retrievePast(100).complete(true));
					nr -= 100;
				} else {
					messages.addAll(channel.getHistory().retrievePast(nr).complete(true));
				}
			
				if (!message.getMentionedUsers().isEmpty()) {
					for (User user : message.getMentionedUsers()) {
						for (Message msg : messages) {
							if (msg.getAuthor() == user) toDelete.add(msg);
						}
					}
				} else {
					toDelete.addAll(messages);
				}
				
				//DELETE MESSAGES
				tchannel.deleteMessages(toDelete).complete(true);
				deleted += 100;
				
			}
			
		} catch (Exception e) {
			//when a message is older than 2 weeks
			//so now either deleting each message seperately or just stopping
			if (toDelete.size() > 100) {
				channel.sendMessage("Removed " + deleted + " messages. " + (total - deleted) + " messages still remain. It is not recommended to bulk delete these.").queue();
			} else {
				Iterator<Message> toDeleteIter = toDelete.iterator();
				while(toDeleteIter.hasNext()) {
					channel.deleteMessageById(toDeleteIter.next().getId()).queue();
				}
			}
			
		}
		try {
			channel.deleteMessageById(message.getId()).complete();
		} catch (Exception e) {
			//np
		}
		
	}

}
