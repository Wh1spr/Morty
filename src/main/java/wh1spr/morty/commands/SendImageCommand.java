package wh1spr.morty.commands;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class SendImageCommand extends Command {

	public SendImageCommand(String imageUrl, String name, String... aliases) {
		super(name, aliases);
		this.imageUrl = imageUrl;
	}
	
	private final String imageUrl;
	
	private static final HashMap<String, Long> timer = new HashMap<String, Long>(); //channel ID
	public static final int timeout = 5000; // timeout in milliseconds, public so i can dynamically change it with eval

	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.MEMBER, invoker.getUser(), false)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		File image = new File(imageUrl);
		
		if (Permission.hasPerm(Permission.ADMIN, invoker.getUser(), false)) {
			channel.sendFile(image, null).queue();
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
		} else if (canUse(channel)) {
			channel.sendFile(image, null).queue();
			message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
			timer.put(channel.getId(), System.currentTimeMillis());
		} else {
			Double outputTime = Math.ceil((timeout - (System.currentTimeMillis() - timer.get(channel.getId()))) / 1000);
			int out = outputTime.intValue() + 1;
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			channel.sendMessage(":x: This command is on cooldown for " + out + " seconds.").queue();
		}
		
	}

	private static boolean canUse(TextChannel channel) {
		
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
