package wh1spr.bot.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public abstract class Command {

	public Command(String name, long perm, String... aliases) {
		if (name == null) throw new IllegalArgumentException("Name cannot be null.");
		this.name = name;
		for (String alias : aliases) {
			this.aliases.add(alias);
		}
		this.CMD_PERM = perm;
	}
	
	@Deprecated
	public Command(String name, String... aliases) {
		this(name, 0<<1, aliases);
	}
	
	String getName() {
		return this.name;
	}
	
	List<String> getAliases() {
		return this.aliases;
	}
	
	protected String name = null;
	protected ArrayList<String> aliases = new ArrayList<String>();
	
	// Enable/Disable
	private boolean isDisabled = false;
	public boolean isDisabled() {return isDisabled;}
	public void disable() {isDisabled = true;}
	public void enable() {isDisabled = false;}
	
	// Maelstrom only
	private boolean mOnly = true;
	protected void setMaelstromOnly(boolean toggle) {this.mOnly = toggle;}
	public boolean isMaelstromOnly() {return this.mOnly;}
	
	// Calls
	public abstract void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args);
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		onCall(jda, null, channel, invoker, message, args);
	}
	
	public void cantUse(MessageChannel channel) {
		channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You can't use this command in a private message.").build()).queue();
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
	
	// CanUse and Perms
	protected final long CMD_PERM;
	
	public static final long PERM_BOT_OWNER = 1 << 63;
	public static final long PERM_SERVER_OWNER = 1 << 62;
	public static final long PERM_ADMIN = 1 << 61;
	
	
	public static final long PERM_EVERYONE = 1 << 1;
}