package wh1spr.bot.dummy;

import net.dv8tion.jda.core.JDA;
import wh1spr.bot.Main;
import wh1spr.bot.command.CommandRegistry;
import wh1spr.bot.command.ImageRegistry;
import wh1spr.bot.dummy.database.DatabaseDummy;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public abstract class Bot {
	
	protected Logger log;
	
	public static final String OWNER = "204529799912226816";
	public static final String MAELSTROM = "433693780571586580";
	
	private final String LOGIN_TOKEN;
	private final String PREFIX;
	private final String DATA_PATH;
	
	private final CommandRegistry commandRegistry;
	private final ImageRegistry imageRegistry;
	
	private DatabaseDummy db;
	private AutoEventHandlerDummy autoEvents;
	
	protected Bot(String token, String dataPath, String prefix) {
		this.LOGIN_TOKEN = token;
		this.PREFIX = prefix;
		this.DATA_PATH = dataPath;
		
		this.commandRegistry = new CommandRegistry(this);
		this.imageRegistry = new ImageRegistry(this);
		
		this.db = new DatabaseDummy(this); // Database can be overridden with protected setDb(x)
		this.autoEvents = new AutoEventHandlerDummy(this);
	}
	
	protected JDA jda = null;
	
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
	
	public DatabaseDummy getDb() {
		return this.db;
	}
	
	protected void setDb(DatabaseDummy db) {
		this.db = db;
	}
	
	public AutoEventHandlerDummy getAutoEvents() {
		return this.autoEvents;
	}
	
	protected void setAutoEvents(AutoEventHandlerDummy auto) {
		this.autoEvents = auto;
	}
	
	public Logger getLog() {
		return this.log;
	}
	
	public void shutdown() {
		shutdownBot();
		if (Main.getNrOfBots() <= 0) {
			shutdownLast();
		}
	}
	
	public void shutdownLast() {
		LoggerCache.getLogger("MAIN").info("Last bot shut down. Shutting down application.");
		LoggerCache.shutdown();
		System.exit(0);
	}
	
	public abstract void shutdownBot();
}
