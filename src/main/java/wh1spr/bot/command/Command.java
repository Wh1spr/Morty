package wh1spr.bot.command;

import java.util.ArrayList;
import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class Command {

	public Command(String name, String... aliases) {
		this.name = name;
		for (String alias : aliases) {
			this.aliases.add(alias);
		}
	}
	
	String getName() {
		return this.name;
	}
	
	List<String> getAliases() {
		return this.aliases;
	}
	
	protected String name = null;
	protected ArrayList<String> aliases = new ArrayList<String>();
	
	private boolean isDisabled = false;
	public boolean isDisabled() {
		return isDisabled;
	}
	
	public void disable() {
		isDisabled = true;
	}
	public void enable() {
		isDisabled = false;
	}
	
	private boolean mOnly = true;
	protected void setMaelstromOnly(boolean toggle) {
		this.mOnly = toggle;
	}
	
	public boolean isMaelstromOnly() {
		return this.mOnly;
	}
	
	public abstract void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args);

	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		onCall(jda, null, channel, invoker, message, args);
	}
	
	public static void failure(Message message) {
		message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
	}
	public static void warning(Message message) {
		message.addReaction(EmojiManager.getForAlias("warning").getUnicode()).queue();
	}
	public static void success(Message message) {
		message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
	}



}