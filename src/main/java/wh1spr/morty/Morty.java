package wh1spr.morty;

import java.util.List;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import wh1spr.morty.command.*;
import wh1spr.morty.command.CommandRegistry.CommandEntry;
import wh1spr.morty.commands.*;

/*
 * I'm well aware that Morty can not handle users that joined when he was offline.
 * I'm probably gonna implement some function to handle this in the future, but for now
 * this is not necessary.
 */
public class Morty {
	
	public static final String MORTY_TOKEN = "[REDACTED]";
	public static final String PREFIX = ".";
	
	public static final CommandRegistry commandRegistry = new CommandRegistry();
	public static final ImageRegistry imageRegistry = new ImageRegistry();
	
	public static JDA jda = null;

	public static void main(String[] args) {
		registerCommands();
		Database.start();
		jda = run();
//		Database.test();
		Database.updateNames(jda);
		try {
			jda.getTextChannelById(C.CHANNEL_INTRODUCTION).getPermissionOverride(jda.getGuildById(C.GUILD).getPublicRole()).getManager().grant(net.dv8tion.jda.core.Permission.MESSAGE_WRITE).complete(true);
		} catch (RateLimitedException e) {/*never happening*/}
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
		
		// User Commands
		commandRegistry.registerCommand(new RoleCommand("role"));
		commandRegistry.registerCommand(new ChannelInputCommand("rules", C.CHANNEL_RULES));
		commandRegistry.registerCommand(new ChannelInputCommand("commands", C.CHANNEL_COMMANDS, "help"));
		
		// Image Commands
		commandRegistry.registerCommand(new AddImageCommand("addimage"));
		commandRegistry.registerCommand(new RemoveImageCommand("removeimage"));
		imageRegistry.registerAllCommands();
		
	}
	
	public static JDA run() {
		JDA jda = null;
		try {
			jda = new JDABuilder(AccountType.BOT)
			        .setToken(MORTY_TOKEN).addEventListener(new CommandHandler(PREFIX, commandRegistry), new CommandHandler(PREFIX, imageRegistry), new AutoEventHandler())
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
		// So basically I'm stopping the use of #introduction, since the database needs to be updated whenever there is a new message.
		try {
			jda.getTextChannelById(C.CHANNEL_INTRODUCTION).getPermissionOverride(jda.getGuildById(C.GUILD).getPublicRole()).getManager().deny(net.dv8tion.jda.core.Permission.MESSAGE_WRITE).complete(true);
		} catch (RateLimitedException e) {/*never happening*/}
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
	}
	
	public static void logError(String msg) {
		System.err.println("[MORTY] ERROR: " + msg);
	}
}
