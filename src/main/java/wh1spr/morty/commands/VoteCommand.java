package wh1spr.morty.commands;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import wh1spr.morty.C;
import wh1spr.morty.Morty;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class VoteCommand extends Command {

	public VoteCommand(String name) {
		super(name);
	}

	// If wrongly formatted, it just doesnt care
	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.WINA, invoker.getUser(), true)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		try {
			
			ArrayList<Emoji> emoticons = new ArrayList<Emoji>();
			
			String[] arguments = message.getContentDisplay().replaceFirst(Morty.PREFIX + this.name, "").replaceAll("\n", "").trim().split("\\[");
			
			String newMessage = "**VOTE** - *" + arguments[0].split(" ", 2)[1].trim() + "*\n";
			
			for (int i = 1; i < arguments.length; i++) {
				try {
					emoticons.add(EmojiManager.getByUnicode(arguments[i].substring(0, arguments[i].indexOf("]")).replaceAll(":", "").trim()));
					newMessage += arguments[i].substring(0, arguments[i].indexOf("]")).trim() + " - " + arguments[i].substring(arguments[i].indexOf("]") + 1).trim() + "\n";
			
				} catch (Exception e) {
					emoticons.add(null);
					break;
				}
			}
			if (emoticons.contains(null)) {
				channel.sendMessage(":x: Please don't use custom emoticons.").queue();
				return;
			}
			
			try {
				
				Message msg = channel.sendMessage(newMessage).complete(true);
				msg.pin().queue();
				if (!startVote(msg.getId(), arguments[0], channel, arguments[0].split(" ", 2)[1], emoticons)) {
					channel.deleteMessageById(msg.getId()).queue();
				} else {
					jda.getTextChannelById(C.CHANNEL_BOT_VOTES).sendMessage(":white_check_mark: New succesful vote creation. Creation command:").queue();
					jda.getTextChannelById(C.CHANNEL_BOT_VOTES).sendMessage(message.getContentDisplay()).queue();
					for (Emoji emo : emoticons) {
						msg.addReaction(emo.getUnicode()).queue();
					}
					message.delete().queue();
				}
			} catch (RateLimitedException e) {
				//cant happen
			}
		} catch (Exception e) {
			channel.sendMessage(":x: Something went wrong. Please review your input.").queue();
		}
		
		
	}
	
	
	
	//The task which you want to execute
	private class TimedTask extends TimerTask
	{
		public TextChannel channel;
		
		public TimedTask(String id, TextChannel channel, String title, ArrayList<Emoji> emoticons) {
			this.id = id;
			this.channel = channel;
			this.title = title;
			this.emoticons = emoticons;
		}
		
		private String id;
		private String title;
		private ArrayList<Emoji> emoticons;
		
	    public void run() {
	    	Message msg = null;
	    	try {msg = channel.getMessageById(this.id).complete(true);} catch (RateLimitedException e) {}
	    	
	    	HashMap<Emoji, Integer> count = new HashMap<Emoji, Integer>();
	    	
	    	msg.getReactions().forEach(react->{
	    		try{count.put(EmojiManager.getByUnicode(react.getReactionEmote().getName()), react.getCount());} catch (Exception e) {/* nothing */}});
	    	
	    	for (Emoji key : count.keySet()) {
	    		if (!emoticons.contains(key)) {
	    			count.remove(key);
	    		}
	    	}
	    	
	    	Emoji winner = null;
	    	while (winner ==  null) {
	    		winner = count.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
	    		if (!emoticons.contains(winner)) {
	    			winner = null;
	    			count.remove(winner);
	    		}
	    		if (count.isEmpty()) {
	    			System.out.println("[Morty] ERROR: No emoticons matched the vote emoticons.");
	    			return;
	    		}
	    	}
//	    	System.out.println(winner.getAliases().get(0));
	    	
	    	String winnerMessage;
	    	try {
	    		winnerMessage = msg.getContentDisplay().substring(msg.getContentDisplay().indexOf(winner.getUnicode()), msg.getContentDisplay().indexOf("\n", msg.getContentDisplay().indexOf(winner.getUnicode())));
	    	} catch (IndexOutOfBoundsException e) {
	    		winnerMessage = msg.getContentDisplay().substring(msg.getContentDisplay().indexOf(winner.getUnicode()));
	    	}
	    	try {
	    		channel.sendMessage("**Vote \"*"+ title.trim() + "*\" has ended. **\n**WINNER:** *" + winnerMessage + "*").queue();
	    		msg.unpin().queue();
	    		
	    	} catch (Exception e) {
	    		// Should never happen, see complete(true) for more info
	    	}
	        
	    	String results = "**Vote \"*"+ title.trim() + "*\" has ended. **\n**WINNER:** *" + winnerMessage + "*";
	    	String next;
	    	for (Emoji result : emoticons) {
	    		try {
		    		next = msg.getContentDisplay().substring(msg.getContentDisplay().indexOf(result.getUnicode()), msg.getContentDisplay().indexOf("\n", msg.getContentDisplay().indexOf(result.getUnicode())));
		    	} catch (IndexOutOfBoundsException e) {
		    		next = msg.getContentDisplay().substring(msg.getContentDisplay().indexOf(result.getUnicode()));
		    	}
	    		results += "\n" + next + " **with " + count.get(result) + " votes.**";
	    	}
	    	msg.delete().queue();
	        channel.getJDA().getTextChannelById(C.CHANNEL_BOT_VOTES).sendMessage(results).queue();
	    }
	}

	private boolean startVote(String id, String time, TextChannel channel, String title, ArrayList<Emoji> emoticons) {

	    //the Date and time at which you want to execute
	    DateFormat dateFormatter = new SimpleDateFormat("dd:HH:mm:ss");
	    dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	    
	    Date date = new Date();
	    try {
			date = dateFormatter.parse(time);
			
		} catch (ParseException e) {
			channel.sendMessage(":x: Time wasn't formatted properly. Please use `dd:HH:mm:ss`").queue();
			return false;
		}
	    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	    
	    c.add(Calendar.SECOND, (int) (date.getTime()/1000));
	    c.add(Calendar.DAY_OF_YEAR, 1);
	    
	    Timer timer = new Timer();

	    //Use this if you want to execute it once
	    timer.schedule(new TimedTask(id, channel, title, emoticons), c.getTime());
	    return true;
	}

}
