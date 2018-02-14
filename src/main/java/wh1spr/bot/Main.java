package wh1spr.bot;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import wh1spr.bot.morty.Morty;
import wh1spr.logger.Logger;

// TODO
// - start a logger here or straight to console?
public class Main {
	
	private static final String propertiesPath = "data/main.properties";
	private static Properties properties = null;
	private static Logger log = new Logger("data/startup.log");
	
	private static ArrayList<Object> bots = new ArrayList<Object>();

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
		if(properties.getProperty("MORTY-START") == "True")  {
			log.info("Trying to start MORTY...");
			String key = properties.getProperty("MORTY-KEY");
			String dataPath = properties.getProperty("MORTY-DATA", "data/morty/");
			String prefix = properties.getProperty("MORTY-PREFIX");
			if (prefix == null) {
				log.warning("Prefix for MORTY could not be found in main.properties. Falling back to default '!'.");
			}
			if (key == null) {
				log.error("Key for MORTY could not be found in main.properties. Morty has not started.");
				return; // Morty can not start
			}
			
			log.info("Running MORTY...");
			bots.add(new Morty(key, dataPath, prefix)); // This will also run Morty
			
		}
	}
}
