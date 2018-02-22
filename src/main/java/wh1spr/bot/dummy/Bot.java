package wh1spr.bot.dummy;

import net.dv8tion.jda.core.JDA;
import wh1spr.bot.command.CommandRegistry;
import wh1spr.bot.command.ImageRegistry;

public abstract class Bot {
	
	public static final String OWNER = "204529799912226816";
	private final String LOGIN_TOKEN;
	private final String PREFIX;
	private final String DATA_PATH;
	
	private final CommandRegistry commandRegistry;
	private final ImageRegistry imageRegistry;
	
	protected Bot(String token, String dataPath, String prefix) {
		this.LOGIN_TOKEN = token;
		this.PREFIX = prefix;
		this.DATA_PATH = dataPath;
		
		this.commandRegistry = new CommandRegistry(this);
		this.imageRegistry = new ImageRegistry(this);
	}
	
	public JDA jda = null;
	
	public abstract void registerCommands();
	public abstract JDA run();
	
	public JDA getJDA() {
		return this.jda;
	}
	
	public String getPrefix() {
		return this.PREFIX;
	}
	
	public String getDataPath() {
		return this.DATA_PATH;
	}
	
	protected String getToken() {
		return this.LOGIN_TOKEN;
	}
	
	public CommandRegistry getCommandRegistry() {
		return this.commandRegistry;
	}
	
	public ImageRegistry getImageRegistry() {
		return this.imageRegistry;
	}
	
	public abstract void shutdown();
}
