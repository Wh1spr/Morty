package wh1spr.morty.commands;

import java.util.HashSet;
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
	// &&clean <int> 					Removes <int> past messages.
	// &&clean @user1 @user2...			Removes messages by these users in the past 99 messages.
	// &&clean <int> @user1 @user2...	Removes messages by these users int he past <int> messages.
	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		try {
		
			if (!message.getMentionedUsers().isEmpty() && message.getMentionedUsers().size() < args.size()) {
				int nr = 0;
				try {
					nr = Integer.valueOf(args.get(0));
				} catch (Exception e) {
					return;
				}
				if (nr > 99) nr = 99; //max amount that can be deleted.
				nr++; //for the clean message itself.
				
				List<Message> messages = channel.getHistory().retrievePast(nr).complete();
				Set<Message> toDelete = new HashSet<Message>(); 
				
				for (User user : message.getMentionedUsers()) {
					for (Message msg : messages) {
						if (msg.getAuthor() == user) toDelete.add(msg);
					}
				}
				toDelete.add(message);
				channel.deleteMessages(toDelete).queue();
			} else if (!message.getMentionedUsers().isEmpty()) {
				List<Message> messages = channel.getHistory().retrievePast(100).complete();
				Set<Message> toDelete = new HashSet<Message>(); 
				
				for (User user : message.getMentionedUsers()) {
					for (Message msg : messages) {
						if (msg.getAuthor() == user) toDelete.add(msg);
					}
				}
				toDelete.add(message);
				channel.deleteMessages(toDelete).queue();
			} else if (args.size() == 1) {
				int nr = 0;
				try {
					nr = Integer.valueOf(args.get(0));
				} catch (Exception e) {
					return;
				}
				if (nr > 99) nr = 99; //max amount that can be deleted.
				nr++; //for the clean message itself.
				
				channel.deleteMessages(channel.getHistory().retrievePast(nr).complete()).queue();
			}
		} catch (Exception e) {
			//when a message is older than 2 weeks
			//so now either deleting each message seperately or just stopping
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
		}
	}

}
