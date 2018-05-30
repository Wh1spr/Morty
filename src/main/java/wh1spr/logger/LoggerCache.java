package wh1spr.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

// ADD LOGGING FOR MAIN
public class LoggerCache {

	private static HashMap<String, Logger> cache = new HashMap<String, Logger>();
	private static PrintWriter mainOut = null;
	private static Logger main = null;
	
	
	public static void start(String url) {
		try {
			new File(url).mkdirs();
			mainOut = new PrintWriter(new BufferedWriter (new FileWriter(url, true)));
			main = getLogger("MAIN");
		} catch (IOException e) {
			System.err.println("Logger startup failed, exiting...");
			System.exit(1);
		}
	}
	
	/**
	 * Returns a {@link Logger} with the given name.
	 * @param name Name of the logger, this should be descriptive of where the logger is used.
	 * @return {@link Logger} with the given name.
	 */
	public static Logger getLogger(String name) {
		Logger l = cache.get(name.toUpperCase());
		if (l == null) return newLogger(name.toUpperCase());
		else return l;
	}
	
	private static Logger newLogger(String name) {
		name = name.toUpperCase();
		Logger log = new Logger(name, mainOut);
		cache.put(name, log);
		return log;
	}
	
	@Deprecated
	public static Logger newLogger(String name, String url) {
		return newLogger(name);
	}
	
	public static void shutdown() {
		Iterator<Logger> iter = cache.values().iterator();
		while (iter.hasNext()) {
			Logger log = iter.next();
			if(!log.getName().equals("MAIN")) {
				log.shutdown();
				iter.remove();
			}
		}
		main.info("All loggers shutdown. Closing down MAIN logger.");
		main.shutdown();
	}

	public static void removeLogger(String name) {
		Logger l = cache.get(name.toUpperCase());
		if (l == null) return;
		removeLogger(l);
	}
	
	public static void removeLogger(Logger log) {
		log.shutdown();
		cache.remove(log.getName());
	}
	
	public static void flush() {
		mainOut.flush();
	}
}
