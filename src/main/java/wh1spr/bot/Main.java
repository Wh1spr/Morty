package wh1spr.bot;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import net.dv8tion.jda.core.OnlineStatus;
import wh1spr.bot.commands.economy.util.EconomyStatus;
import wh1spr.bot.database.Database2;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.mongodb.Mongo;
import wh1spr.bot.morty.Morty;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class Main {
	
	private static final String propertiesPath = "data/main.properties";
	public static Properties properties = null;
	private static Logger log = null;
	
	private static Bot bot = null;

	public static void main(String[] args) {
		if(!Files.exists(Paths.get(propertiesPath))) {
			System.err.println("FATAL - main.properties is not present.");
			System.err.println("FATAL - Bots can not be started without this file.");
		    System.exit(1);
		}
		
		try {
			properties = new Properties();
			properties.load(new FileInputStream(new File(propertiesPath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Launch Order
		 * 1. LoggerCache
		 * 2. Database
		 * 3. Bots
		 * 4. EconomyStatus
		 */
		LoggerCache.start("data/logs/main-%s.log");
		LoggerCache.setLevel(LoggerCache.DEBUG);
		log = LoggerCache.getLogger("MAIN");
		
		log.info("Starting Database...");
		Mongo.start();
		
		log.info("Starting Bot...");
		morty();
		
		log.info("Starting Old database...");
		Database2.start(bot.getJDA());
		
		log.info("Starting EconomyStatus...");
		EconomyStatus.start();
		
		bot.getJDA().getPresence().setStatus(OnlineStatus.ONLINE);
		
	}
	
	private static void morty() {
		if(properties.getProperty("MORTY-START", "False").equals("True"))  {
			String key = properties.getProperty("MORTY-KEY");
			String dataPath = properties.getProperty("MORTY-DATA", "data/Morty/");
			String prefix = properties.getProperty("MORTY-PREFIX");
			log.debug("[Morty] Token - " + key);
			log.debug("[Morty] DataPath - " + dataPath);
			log.debug("[Morty] Prefix - '" + prefix + "'");
			if (prefix == null) {
				log.warning("Prefix for MORTY could not be found in main.properties. Falling back to default '!'.");
				prefix = "!";
			}
			if (key == null) {
				log.fatal("Key for MORTY could not be found in main.properties. Morty has not started.");
				LoggerCache.shutdown();
				System.exit(1);
			}
			
			log.info("Running MORTY...");
			bot = new Morty(key, dataPath, prefix); // This will also run Morty
			
		}
	}
	
	public static Bot getBot() {
		return bot;
	}
}
