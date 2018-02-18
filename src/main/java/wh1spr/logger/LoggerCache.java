package wh1spr.logger;

import java.util.HashMap;
import java.util.Iterator;

// ADD LOGGING FOR MAIN
public class LoggerCache {

	private static HashMap<String, Logger> cache = new HashMap<String, Logger>();
	
	public static Logger getLogger(String name) {
		name = name.toUpperCase();
		return cache.get(name);
	}
	
	public static Logger newLogger(String name, String url) {
		name = name.toUpperCase();
		Logger log = new Logger(name, url);
		cache.put(name, log);
		return log;
	}
	
	public static void shutdown() {
		Iterator<Logger> iter = cache.values().iterator();
		while (iter.hasNext()) {
			Logger log = iter.next();
			if(!log.getName().equals("MAIN")) {
				removeLogger(log);
			}
		}
	}

	public static void removeLogger(String name) {
		removeLogger(getLogger(name));
	}
	
	public static boolean removeLogger(Logger log) {
		if(!log.isClosed()) {
			return log.shutdown();
		} else {
			return cache.remove(log.getName().toUpperCase(), log);
		}
	}
}
