package wh1spr.bot.commandrework;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public abstract class Command {
	
	private Logger log = LoggerCache.getLogger("CMD");

	public Command(String id, Permission perm, String name, String... aliases) {
		if (id == null) throw new IllegalArgumentException("Command ID cannot be null.");
		if (name == null) throw new IllegalArgumentException("Name cannot be null.");
		
		log.debug("Creating command " + id + "  with default permission " + perm.getName());
		this.id = id.toLowerCase();
		
		if (perm.isGuild()) this.setGuildOnly(true);
		this.perm = perm;
		
		this.name = name.toLowerCase();
		for (String alias : aliases) {
			this.aliases.add(alias.toLowerCase());
		}
		
		
	}
	
	public String getId() {
		return this.id;
	}
	
	/**
	 * If return is null, then it is DEV
	 */
	public Permission getPermission() {
		return this.perm;
	}
	
	public String getName() {
		return this.name;
	}
	
	List<String> getAliases() {
		return this.aliases;
	}
	
	private final String id;
	private final Permission perm;
	protected String name = null;
	protected ArrayList<String> aliases = new ArrayList<String>();
	
	protected String usage = "No usage defined.";
	protected String desc = "No description defined.";
	protected String category = "Misc"; // DEFAULT
	
	/**
	 * Sets the usage String that is used
	 * 1. When user failed to use the command correctly.
	 * 2. In the command-specific help command.
	 * @param usage The usage string
	 */
	protected void setUsage(String usage) {
		this.usage = usage;
	}
	
	/**
	 * Sets a short description of the command. This is used
	 * 1. In the general help command (for that category)
	 * 2. In the command-specific help command.
	 * @param desc
	 */
	protected void setDescription(String desc) {
		this.desc = desc;
	}

	/**
	 * Sets the category of the command. Default category is <i>"Misc"</i>
	 * @param cat The category of the command
	 */
	protected void setCategory(String cat) {
		String res = "";
		for (String w : cat.toLowerCase().split(" ")) {
			res += w.substring(0, 1).toUpperCase() + w.substring(1) + " ";
		}
		this.category = res.trim();
	}
	
	public String getUsage() {
		return this.usage;
	}
	public String getDescription() {
		return this.desc;
	}
	public String getCategory() {
		return this.category;
	}
	
	// Enable/Disable
	private boolean isDisabled = false;
	public boolean isDisabled() {return isDisabled;}
	public void disable() {isDisabled = true;}
	public void enable() {isDisabled = false;}
	
	// Guild only
	private boolean gOnly = false;
	protected void setGuildOnly(boolean toggle) {
		if (this.getPermission().isGuild() && !toggle) throw new IllegalArgumentException("Tried to set GuildOnly to false, but permission is Guild-only.");
		this.gOnly = toggle;
	}
	public boolean isGuildOnly() {return this.gOnly;}
	
	// Maelstrom only
	private boolean mOnly = false;
	protected void setMaelstromOnly(boolean toggle) {this.mOnly = toggle;}
	public boolean isMaelstromOnly() {return this.mOnly;}
	
	// Calls
	public abstract void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args);
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		onCall(jda, null, channel, invoker, message, args);
	}
}
