package wh1spr.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;


public class Logger {

	private final String name;
	private PrintWriter out = null;
	
	private LocalDateTime time = LocalDateTime.now();
	
	Logger(String name, String url) {
		this.name = name;
		try {
			this.out = new PrintWriter(new BufferedWriter (new FileWriter(url)));
		} catch (IOException e) {
			if (name.equals("MAIN")) {
				System.err.println("FATAL - Logger startup failed. Shutting down application.");
				e.printStackTrace();
				System.exit(1);
			} else {
				LoggerCache.getLogger("MAIN").error(e, "Logger startup for logger " + name + " at location '" + url + "' failed.");
			}
		}
		
		info("Logger startup - Hello!");
	}
	
	public void error(Exception e, String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + " ERROR - " + msg);
		e.printStackTrace(out);
		out.flush();
	}
	
	public void error(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + " ERROR - " + msg);
		out.flush();
	}
	
	public void info(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + " INFO - " + msg);
		out.flush();
	}
	
	public void debug(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + " DEBUG - " + msg);
		out.flush();
	}
	
	public void warning(String msg) {
		String time = "[" + getTime() + "]";
		out.println(time + " WARNING - " + msg);
		out.flush();
	}
	
	public String getTime() {
		return time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
	}
	public String getDateTime() {
		return time.getYear() + "/" + time.getMonthValue() + "/" + time.getDayOfMonth() + " - " + getTime();
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean shutdown() {
		out.flush();
		out.close();
		isClosed = true;
		return LoggerCache.removeLogger(this);
	}
	
	private boolean isClosed = false;
	public boolean isClosed() {
		return isClosed;
	}
}
