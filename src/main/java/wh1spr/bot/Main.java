package wh1spr.bot;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

import wh1spr.bot.dummy.Bot;
import wh1spr.bot.morty.Morty;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

public class Main {
	
	private static final String propertiesPath = "data/main.properties";
	private static Properties properties = null;
	private static Logger log = LoggerCache.newLogger("MAIN", "data/main.log");
	
	private static HashMap<String, Bot> bots = new HashMap<String, Bot>();

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
		
		morty();
		
	}
	
	private static void morty() {
		if(properties.getProperty("MORTY-START", "False").equals("True"))  {
			log.info("Trying to start MORTY...");
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
				log.error("Key for MORTY could not be found in main.properties. Morty has not started.");
				return; // Morty can not start
			}
			
			log.info("Running MORTY...");
			bots.put("MORTY", new Morty(key, dataPath, prefix)); // This will also run Morty
			
		}
	}
	
	public static Bot getBot(String name) {
		return bots.get(name);
	}
	
	public static int getNrOfBots() {
		return bots.values().size();
	}
	
	public static void removeBot(String name) {
		bots.remove(name);
	}
}
