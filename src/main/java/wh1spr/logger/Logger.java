package wh1spr.logger;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private final String name;
	private PrintWriter out = null;
	private int level = 1;
	
	Logger(String name, PrintWriter out, int lvl) {
		this.name = name.toUpperCase();
		this.out = out;
		this.level = lvl;
		info("Logger startup at " + getDateTimeStamp());
	}
	
	void setLvl(int level) {
		this.level = level;
	}
	
	public void error(Exception e, String msg) {
		if (this.level > LoggerCache.ERROR) return;
		out.println(String.format("[%s][%s][ERROR] %s", getTimeStamp(), this.getName(), msg));
		e.printStackTrace(out);
	}
	
	public void error(String msg) {
		if (this.level > LoggerCache.ERROR) return;
		out.println(String.format("[%s][%s][ERROR] %s", getTimeStamp(), this.getName(), msg));
	}
	
	public void fatal(Exception e, String msg) {
		if (this.level > LoggerCache.FATAL) return;
		out.println(String.format("[%s][%s][FATAL] %s", getTimeStamp(), this.getName(), msg));
		e.printStackTrace(out);
	}
	
	public void fatal(String msg) {
		if (this.level > LoggerCache.FATAL) return;
		out.println(String.format("[%s][%s][FATAL] %s", getTimeStamp(), this.getName(), msg));
	}
	
	public void info(String msg) {
		if (this.level > LoggerCache.INFO) return;
		out.println(String.format("[%s][%s][INFO] %s", getTimeStamp(), this.getName(), msg));
	}
	
	public void debug(String msg) {
		if (this.level > LoggerCache.DEBUG) return;
		out.println(String.format("[%s][%s][DEBUG] %s", getTimeStamp(), this.getName(), msg));
	}
	
	public void warning(String msg) {
		if (this.level > LoggerCache.WARNING) return;
		out.println(String.format("[%s][%s][WARNING] %s", getTimeStamp(), this.getName(), msg));
	}
	
	public String getName() {
		return this.name;
	}
	
	public static String getDateTimeStamp() {
		return getDateStamp() + "_" + getTimeStamp();
	}
	
	public static String getDateStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	public static String getTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    return strDate;
	}
	
	void setOut(PrintWriter newOut) {
		this.out = newOut;
	}
	
	private boolean shutdown = false;
	public void shutdown() {
		if (shutdown) return;
		shutdown = true;
		if (out == null) return; //already shut down
		info("Logger shutting down. Goodbye!");
		out = null;
		LoggerCache.removeLogger(this);
	}
}
