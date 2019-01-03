package wh1spr.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoggerCache {

	private static HashMap<String, Logger> cache = new HashMap<String, Logger>();
	private static String url = null;
	private static PrintWriter mainOut = null;
	private static Logger main = null;
	private static Timer t = null;
	
	public static void start(String url) {
		LoggerCache.url = url;
		try {
			new File(url.substring(0, url.lastIndexOf('/'))).mkdirs();
			mainOut = new PrintWriter(new FileWriter(String.format(url, getDateStamp()), true));
			main = getLogger("MAIN");
		} catch (IOException e) {
			System.err.println("Logger startup failed, exiting...");
			System.exit(1);
		}
		
		Calendar today = Calendar.getInstance();
		today.add(Calendar.DAY_OF_YEAR, 1);
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 1);

		t = new Timer();
		t.schedule(new DailyCycle(), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
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
	
	/**
	 * Shuts down all currently active loggers.
	 */
	public static void shutdown() {
		ArrayList<Logger> copy = new ArrayList<Logger>(cache.values());
		Iterator<Logger> iter = copy.iterator();
		while (iter.hasNext()) {
			Logger log = iter.next();
			if(!log.getName().equals("MAIN")) {
				log.shutdown();
			}
		}
		main.info("All loggers shutdown. Closing down MAIN logger.");
		main.shutdown();
	}

	
	public static void removeLogger(Logger log) {
		log.shutdown();
		cache.remove(log.getName());
	}
	
	private static class DailyCycle extends TimerTask {
		@Override
		public void run() {
			PrintWriter oldOut = LoggerCache.mainOut;
			Logger old = new Logger("LOG-TRANSFER", oldOut, level);
			old.info("Logger transferring to new file. Closing this file!");
			old.shutdown();
			
			try {
				final PrintWriter newOut = new PrintWriter(new FileWriter(String.format(url, getDateStamp()), true));
				LoggerCache.mainOut = newOut;
				
				LoggerCache.cache.values().forEach(el->{
					el.setOut(newOut);
				});
				main.info("New logfile created, hello!");
			} catch (IOException e) {/*Didn't happen before, shouldn't happen now*/}
			
			oldOut.close();
		}
	}
	
	public static String getDateStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
}
