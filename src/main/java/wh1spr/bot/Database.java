package wh1spr.bot;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wh1spr.bot.dummy.Bot;
import wh1spr.logger.LoggerCache;

public class Database {

	private static Connection conn = null;
	private Bot bot;
	
	public Database(Bot bot) {
		this.bot = bot;
		if(conn == null) conn = getConn();
	}
	
	public Connection getConn() {
		if (conn != null) return conn;
		
		Connection con2 = null;
		
		String url = "jdbc:sqlite:data/Morty2.db";
		try {
			Class.forName("org.sqlite.JDBC");
			if(!Files.exists(Paths.get("data/Morty2.db"))) {
				LoggerCache.getLogger("MAIN").error("Morty2 Database is not present and application cannot run. Exiting...");
				bot.shutdown();
			}
			con2 = DriverManager.getConnection(url);
			LoggerCache.getLogger("MAIN").info("Connection to the database has been established.");
		} catch (Exception e) {
			LoggerCache.getLogger("MAIN").error(e, "Could not establish connection to the Database. Exiting...");
		}
		return con2;
	}
	
	public ResultSet executeQuery(String sql) {
		try (Statement stmt  = Database.conn.createStatement();
			 ResultSet rs    = stmt.executeQuery(sql)) {
			return rs;
		} catch (SQLException e) {
			bot.getLog().error(e, "An SQL Query failed to execute: '" + sql + "'");
		}
		return null;
	}
	
	/*
	 * What follows will be multiple standardised queries that can be used by all the bots.
	 * These include new users and new guilds, removing them and updating them.
	 * Because we can have multiple bots in the same guild, we have to somehow let the 
	 * eventhandlers know when to fire an update so we dont flood the database with duplicates.
	 * 
	 * A solution could be to have Main hold a list of guilds and their bots, and have ONLY THE FIRST BOT to be added,
	 * that is also in that guild, fire the update.
	 * 
	 */
	
	
}
