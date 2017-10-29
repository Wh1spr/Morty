package wh1spr.morty;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import wh1spr.morty.command.*;
import wh1spr.morty.commands.*;


public class Morty {
	
	public static final String MORTY_TOKEN = "[REDACTED]";
	public static final String PREFIX = ".";
	
	public static final CommandRegistry commandRegistry = new CommandRegistry();
	
	public static JDA jda = null;

	public static void main(String[] args) {
		registerCommands();
		Database.start();
		jda = run();
//		Database.test();
	}
	
	public static void registerCommands() {
		// Bot Commands
		commandRegistry.registerCommand(new ChangeGameCommand("changegame", "cg"));
		commandRegistry.registerCommand(new ChangeNameCommand("changename", "cn"));
		commandRegistry.registerCommand(new ShutdownCommand("shutdown"));
		commandRegistry.registerCommand(new CommandDisableCommand("disablecommand", "dcmd"));
		
		// Dev Commands
		commandRegistry.registerCommand(new EvalCommand("eval"));
		commandRegistry.registerCommand(new EmojiToUnicodeCommand("emote"));
		
		// Channel Commands
		commandRegistry.registerCommand(new CleanCommand("clean"));
		commandRegistry.registerCommand(new VoteCommand("vote"));
		commandRegistry.registerCommand(new IntroductionCommand("intro"));
		
		// Role Command
		commandRegistry.registerCommand(new RoleCommand("role"));
		
		// Image Commands
//		commandRegistry.registerCommand(new SendImageCommand("C:/Users/VDK/Desktop/morty.jpg", "morty"));
		commandRegistry.registerCommand(new SendImageCommand("images/bitchHoldON.jpg", "holdon"));
		commandRegistry.registerCommand(new SendImageCommand("images/blueeyeswhitepepe.jpg", "pepe-2"));
		commandRegistry.registerCommand(new SendImageCommand("images/BLYATMAN.jpg", "blyatman"));
		commandRegistry.registerCommand(new SendImageCommand("images/dumbass.jpg", "what"));
		commandRegistry.registerCommand(new SendImageCommand("images/future.jpg", "future"));
		commandRegistry.registerCommand(new SendImageCommand("images/gitgud.jpg", "gitgud"));
		commandRegistry.registerCommand(new SendImageCommand("images/sendnudes.jpg", "sendnudes"));
		commandRegistry.registerCommand(new SendImageCommand("images/time_to_stop.jpg", "stop"));
		commandRegistry.registerCommand(new SendImageCommand("images/waitAMin.jpg", "holdup"));
		commandRegistry.registerCommand(new SendImageCommand("images/what.jpg", "wat"));
		commandRegistry.registerCommand(new SendImageCommand("images/whyulying.png", "liar"));
//		commandRegistry.registerCommand(new SendBeautyCommand("images/beauty.jpg", "beauty")); REDUNDANT NOW
		
	}
	
	public static JDA run() {
		JDA jda = null;
		try {
			jda = new JDABuilder(AccountType.BOT)
			        .setToken(MORTY_TOKEN).addEventListener(new CommandHandler(PREFIX, commandRegistry), new AutoEventHandler())
			        .buildBlocking();
		} catch (Exception e) {
			System.out.println("[MORTY] ERROR: Morty could not be initialized. " + e.getClass().getSimpleName());
		}
		return jda;
	}

	public static JDA getJDA() {
		return jda;
	}
	
	public static void shutdown() {
		save();
		Database.close();
		getJDA().shutdown();
		System.exit(0);
	}
	
	public static void save() {
		//save items
		// not necessary for now since nothing needs to be saved.
	}
	
	// Log outs
	public static void logInfo(String msg) {
		System.out.println("[MORTY] INFO: " + msg);
	}
	
	public static void logFatal(String msg) {
		System.out.println("[MORTY] FATAL: " + msg);
	}
	
	public static void logError(String msg) {
		System.out.println("[MORTY] ERROR: " + msg);
	}
}
