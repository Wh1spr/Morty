package wh1spr.bot.morty;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import wh1spr.bot.command.*;
import wh1spr.bot.commands.*;
import wh1spr.bot.dummy.Bot;

/*
 * I'm well aware that Morty can not handle users that joined when he was offline.
 * I'm probably gonna implement some function to handle this in the future, but for now
 * this is not necessary.
 * 
 * So the rest of the project is not ready yet for a non-static version of a Bot.
 * I'll need Main.java to have some way to get these objects and on full shutdown (only allowed by Morty btw)
 * it needs to call all the shutdown functions of all the running bots (if there are multiple)
 * 
 * Basically I'm redoing a lot of stuff so I can make multiple bots from a single jar file, controlled by 
 * a .properties file. This is handy dandy if a friend would want his/her own bot instead of Morty, with
 * the same functions.
 */
public class Morty extends Bot {
	
	public Morty(String key, String dataPath, String prefix) {
		super(key, dataPath, prefix);
		Database.start();
		registerCommands();
		jda = run();
		Database.updateNames(jda);
	}
	
	public void registerCommands() {
		CommandRegistry commandRegistry = this.getCommandRegistry();
		// Bot Commands
		commandRegistry.registerCommand(new ChangeGameCommand("changegame", "cg"));
		commandRegistry.registerCommand(new ChangeNameCommand("changename", "cn"));
		commandRegistry.registerCommand(new ShutdownCommand("shutdown", this));
		commandRegistry.registerCommand(new CommandDisableCommand("disablecommand", this.getCommandRegistry(), "dcmd"));
		commandRegistry.registerCommand(new SendFromMortyCommand("send"));
		
		// Dev Commands
		commandRegistry.registerCommand(new EvalCommand("eval"));
		commandRegistry.registerCommand(new EmojiToUnicodeCommand("emote"));
		
		// Channel Commands
		commandRegistry.registerCommand(new CleanCommand("clean"));
		commandRegistry.registerCommand(new VoteCommand("vote",this));
//		commandRegistry.registerCommand(new IntroductionCommand("intro")); // I'm gonna redo this
		
		// User Commands
//		commandRegistry.registerCommand(new RoleCommand("role")); // Might remake this for custom roles, not sure.
		
		// Image Commands
		commandRegistry.registerCommand(new AddImageCommand("addimage", this.getImageRegistry()));
		commandRegistry.registerCommand(new RemoveImageCommand("removeimage", this.getImageRegistry()));
		this.getImageRegistry().registerAllCommands();
		
	}
	
	public JDA run() {
		JDA jda = null;
		try {
			jda = new JDABuilder(AccountType.BOT)
			        .setToken(this.getToken()).addEventListener(
			        		new CommandHandler(this.getPrefix(), this.getCommandRegistry()),
			        		new CommandHandler(this.getPrefix(), this.getImageRegistry()),
			        		new AutoEventHandler())
			        .buildBlocking();
		} catch (Exception e) {
			logFatal("JDA instance could not be initialized.", e);
		}
		return jda;
	}
	
	public void shutdown() {
		Database.close();
		getJDA().shutdown();
		System.exit(0);
	}
	
	// Log outs
	public static void logInfo(String msg) {
		System.out.println("[MORTY] INFO: " + msg);
	}
	
	public static void logFatal(String msg) {
		System.err.println("[MORTY] FATAL: " + msg);
		System.exit(0);
	}
	
	public static void logFatal(String msg, Exception e) {
		System.err.println("[MORTY] FATAL: " + msg);
		e.printStackTrace();
		System.exit(0);
	}
	
	public static void logError(String msg) {
		System.err.println("[MORTY] ERROR: " + msg);
	}
}
