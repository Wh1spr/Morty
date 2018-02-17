package wh1spr.bot.commands;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import wh1spr.bot.command.Command;
import wh1spr.bot.morty.C;
import wh1spr.bot.morty.Permission;
// Remove c.guild related items.
public class SendImageCommand extends Command {

	public SendImageCommand(String imageUrl, String name, String... aliases) {
		super(name, aliases);
		this.imageUrl = imageUrl;
	}
	
	private final String imageUrl;
	
	private static final HashMap<String, Long> timer = new HashMap<String, Long>(); //channel ID
	public static final int timeout = 5000; // timeout in milliseconds, public so i can dynamically change it with eval

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, Member invoker, Message message, List<String> args) {
		if (guild.getId().equals(C.GUILD)) {
			if (!Permission.hasPerm(Permission.MEMBER, invoker.getUser(), false)) {
				message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
				return;
			}
		}
		
		//the color is the same color as the bot's role
		MessageEmbed msg = new EmbedBuilder().setImage(this.imageUrl).setColor(new Color(16763904)).build();
		
		if (Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
			channel.sendMessage(msg).queue();
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
		} else if (canUse(channel)) {
			channel.sendMessage(msg).queue();
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			timer.put(channel.getId(), System.currentTimeMillis());
		} else {
			message.addReaction(EmojiManager.getForAlias("warning").getUnicode()).queue();
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
