package wh1spr.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

//Change this so it appends and has closedown/startup message + sets some whitespace in between

public class Logger {

	private final String name;
	private PrintWriter out = null;
	
	private LocalDateTime time = LocalDateTime.now();
	
	Logger(String name, String url) {
		this.name = name.toUpperCase();
		try {
			new File("url").mkdirs();
			this.out = new PrintWriter(new BufferedWriter (new FileWriter(url, true)));
		} catch (IOException e) {
			if (name.equals("MAIN")) {
				System.err.println("FATAL - Logger startup failed. Shutting down application.");
				e.printStackTrace();
				System.exit(1);
			} else {
				LoggerCache.getLogger("MAIN").error(e, "Logger startup for logger " + name + " at location '" + url + "' failed.");
			}
		}
		
		info("");
		info("LOGGER STARTUP - " + getDateTime());
		info("");
		out.flush();
	}
	
	public void error(Exception e, String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + "[ERROR] " + msg);
		e.printStackTrace(out);
	}
	
	public void error(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + "[ERROR] " + msg);
	}
	
	public void fatal(Exception e, String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + "[FATAL] " + msg);
		e.printStackTrace(out);
	}
	
	public void fatal(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + "[FATAL] " + msg);
	}
	
	public void info(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + "[INFO] " + msg);
	}
	
	public void debug(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + "[DEBUG] " + msg);
	}
	
	public void warning(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + "[WARNING] " + msg);
	}
	
	public String getTime() {
		return time.getHour() + ":" + String.format("%2d", time.getMinute()).replace(' ', '0') + ":" + String.format("%2d", time.getSecond()).replace(' ', '0');
	}
	public String getDateTime() {
		return time.getYear() + "/" + time.getMonthValue() + "/" + time.getDayOfMonth() + " - " + getTime();
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean shutdown() {
		LoggerCache.getLogger("MAIN").info("Logger " + name + " shutting down.");
		info("Logger has been shutdown.");
		out.flush();
		out.close();
		isClosed = true;
		return LoggerCache.removeLogger(this);
	}
	
	private boolean isClosed = false;
	public boolean isClosed() {
		return isClosed;
	}
	
	public void flush() {
		out.flush();
	}
}
