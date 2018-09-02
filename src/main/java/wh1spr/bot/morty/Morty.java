package wh1spr.bot.morty;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import wh1spr.bot.command.*;
import wh1spr.bot.commands.*;
import wh1spr.bot.commands.dev.*;
import wh1spr.bot.commands.economy.settings.*;
import wh1spr.bot.commands.maelstrom.*;
import wh1spr.bot.commands.economy.*;
import wh1spr.bot.commands.mod.*;
import wh1spr.bot.dummy.Bot;
import wh1spr.logger.LoggerCache;

public class Morty extends Bot {
	
	public Morty(String key, String dataPath, String prefix) {
		super(key, dataPath, prefix);
		this.log = LoggerCache.getLogger("MORTY");
		log.info("Setting AutoEventHandler to custom.");
		this.setAutoEvents(new AutoEventHandlerMorty(this));
		log.info("Registering commands for Morty.");
		this.registerCommands();
		log.info("Starting JDA instance.");
		this.jda = run();
		jda.getPresence().setStatus(OnlineStatus.OFFLINE);
	}
	
	public void registerCommands() {
		CommandRegistry commandRegistry = this.getCommandRegistry();
		// Dev Commands
		commandRegistry.registerCommand(new ChangeGameCommand("changegame", "cg"));
		commandRegistry.registerCommand(new ChangeNameCommand("changename", "cn"));
		commandRegistry.registerCommand(new CommandDisableCommand("disablecommand", this.getCommandRegistry(), "dcmd"));
		commandRegistry.registerCommand(new CommandEnableCommand("enablecommand", this.getCommandRegistry(), "ecmd"));
		commandRegistry.registerCommand(new EmojiToUnicodeCommand("emote"));
		commandRegistry.registerCommand(new EvalCommand("eval", this));
		commandRegistry.registerCommand(new FlushLogCommand("flushlog", "fl"));
		commandRegistry.registerCommand(new FlushEcoCommand("flusheco", "fe"));
		commandRegistry.registerCommand(new ShutdownCommand("shutdown", this, "oof"));
		commandRegistry.registerCommand(new SendFromJDACommand("send"));
		commandRegistry.registerCommand(new SqlExecCommand("sql"));
		
		// Maelstrom Commands
		commandRegistry.registerCommand(new AcceptCommand("accept"));
		
		// Economy Commands
		commandRegistry.registerCommand(new EcoSetupCommand("ecosetup"));
		commandRegistry.registerCommand(new TransferCommand("transfer"));
		commandRegistry.registerCommand(new GrantCommand("grant"));
		commandRegistry.registerCommand(new BalanceCommand("balance", "bal"));
		commandRegistry.registerCommand(new PayCommand("pay"));
		commandRegistry.registerCommand(new DailyCommand("daily"));
		
		// Economy Games Commands
		commandRegistry.registerCommand(new SlotsCommand("slots"));
		
		// Mod Commands
		commandRegistry.registerCommand(new CleanCommand("clean"));
		
		// Other Commands
		commandRegistry.registerCommand(new VoteCommand("vote", this));
//		commandRegistry.registerCommand(new IntroductionCommand("intro")); // I'm gonna redo this
		
		// Fun Commands
		// Morty seems to be German, no fun here.
		log.info("All commands registered.");
		
	}
	
	public JDA run() {
		JDA jda = null;
		try {
			jda = new JDABuilder(AccountType.BOT)
			        .setToken(this.getToken()).addEventListener(
			        		new CommandHandler(this.getPrefix(), this.getCommandRegistry()),
			        		this.getAutoEvents())
			        .buildBlocking();
		} catch (Exception e) {
			log.fatal(e, "JDA instance could not be initialized.");
			shutdown();
		}
		return jda;
	}

	@Override
	public void shutdownBot() {
		if (log == null) log = LoggerCache.getLogger("MORTY");
		log.info("Shutting down Morty.");
		if (jda!=null) getJDA().shutdown();
		log.shutdown();
	}
}
