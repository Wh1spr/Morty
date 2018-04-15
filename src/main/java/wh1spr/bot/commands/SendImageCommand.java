package wh1spr.bot.commands;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Perm;

public class SendImageCommand extends Command {

	public SendImageCommand(String imageUrl, String name, String... aliases) {
		super(name, aliases);
		this.imageUrl = imageUrl;
	}
	
	private final String imageUrl;
	
	private static final HashMap<String, Long> timer = new HashMap<String, Long>(); //channel ID
	public static final int timeout = 5000; // timeout in milliseconds, public so i can dynamically change it with eval

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.has(Perm.TRIAL, invoker)) return;
		
		//the color is the same color as the bot's role
		MessageEmbed msg = new EmbedBuilder().setImage(this.imageUrl).setColor(new Color(16763904)).build();
		
		if (Perm.has(Perm.ADMIN, invoker)) {
			channel.sendMessage(msg).queue();
			success(message);
		} else if (canUse(channel)) {
			channel.sendMessage(msg).queue();
			success(message);
			timer.put(channel.getId(), System.currentTimeMillis());
		} else {
			warning(message);
		}
		
	}

	private static boolean canUse(MessageChannel channel) {
		
		long current = System.currentTimeMillis();
		Long last = timer.get(channel.getId());
		
		if (last == null) {
			return true;
		} else if (current - last > timeout) {
			return true;
		}
		return false;
	}
}
