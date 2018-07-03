package wh1spr.bot.morty;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import wh1spr.logger.LoggerCache;

// TO BE REDONE
@Deprecated
public class Database {
	
	public static Connection conn = null;

	@Deprecated
	public static void start() {
		String url = "jdbc:sqlite:Morty.db";
		try {
			Class.forName("org.sqlite.JDBC");
			if(!Files.exists(Paths.get("Morty.db"))) {
				System.out.println("Database is not present. Exiting...");
				System.exit(0);
			}
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to Database has been established");
		} catch (Exception e) {
			System.out.println("Could not establish connection to the Database. Exiting...");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("[MORTY] ERROR: Connection to database could not be closed.");
			e.printStackTrace();
		} catch (NullPointerException e) {
			//nothing, just needs to be caught...
			System.out.println("[MORTY] INFO: Connection to database has been closed.");
		}
	}
	
	@Deprecated
	public static String getIntroductionId(User user) {
		String sql = "SELECT Intro FROM Users WHERE UserID = " + user.getId();
		
		try (Statement stmt  = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			rs.next();
			String intro = rs.getString("Intro");
			stmt.close();
			
			return intro;
			
		} catch (Exception e) {
			return "0";
		}
	}
	
	@Deprecated
	public static boolean removeImage(String URLorName) {
		String sql = "DELETE FROM Images WHERE Name = '" + URLorName + "' OR URL = '" + URLorName + "'";
		
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (Exception e) {
			LoggerCache.getLogger("MORTY").error(e,"Could not remove Image");
			return false;
		}
	}
	
	// True if it worked, false if there already is one with this name
	@Deprecated
	public static boolean putImage(String URL, String name) {
		if (existsImage(name)) return false; //In this way, it IS possible to have aliases
		
		String sql = "INSERT INTO Images(URL, Name) VALUES('" + URL + "','" + name + "')";
		
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (Exception e) {
			LoggerCache.getLogger("MORTY").error(e,"Could not insert new Image");
			return false;
		}
	}
	
	@Deprecated
	public static boolean existsImage(String name) {
		String sql = "SELECT COUNT(*) AS total FROM Images WHERE Name = '" + name + "'";
		try (Statement stmt  = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			rs.next();
			boolean result = rs.getInt("total") != 0;
			stmt.close();
			return result;
		} catch (Exception e) {
			return false;
		}
	}
	

}
