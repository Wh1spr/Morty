package wh1spr.logger;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private final String name;
	private PrintWriter out = null;
	
	Logger(String name, PrintWriter out) {
		this.name = name.toUpperCase();
		this.out = out;
		info("Logger startup at " + getDateTimeStamp());
	}
	
	public void error(Exception e, String msg) {
		out.println(String.format("[%s][%s][ERROR]", getTimeStamp(), this.getName()));
		e.printStackTrace(out);
	}
	
	public void error(String msg) {
		out.println(String.format("[%s][%s][ERROR]", getTimeStamp(), this.getName()));
	}
	
	public void fatal(Exception e, String msg) {
		out.println(String.format("[%s][%s][FATAL]", getTimeStamp(), this.getName()));
		e.printStackTrace(out);
	}
	
	public void fatal(String msg) {
		out.println(String.format("[%s][%s][FATAL]", getTimeStamp(), this.getName()));
	}
	
	public void info(String msg) {
		out.println(String.format("[%s][%s][INFO]", getTimeStamp(), this.getName()));
	}
	
	public void debug(String msg) {
		out.println(String.format("[%s][%s][DEBUG]", getTimeStamp(), this.getName()));
	}
	
	public void warning(String msg) {
		out.println(String.format("[%s][%s][WARNING]", getTimeStamp(), this.getName()));
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
	
	public void shutdown() {
		if (out == null) return; //already shut down
		info("Logger shutting down. Goodbye!");
		out.flush();
		out = null;
		LoggerCache.removeLogger(this);
	}
}
