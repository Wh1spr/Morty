package wh1spr.morty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dv8tion.jda.core.entities.User;

public class Database {
	
	public static Connection conn = null;

	public static void start() {
		String url = "jdbc:sqlite:Morty.db";
		try {
			conn = DriverManager.getConnection(url);
			System.out.println("[MORTY] INFO: Connection to Database has been established");
		} catch (SQLException e) {
			System.out.println("[MORTY] ERROR: Could not establish connection to the Database. Exiting...");
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
	
	public static void remove(User user) {
		String sql = "DELETE FROM Users WHERE UserID = " + user.getId();
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.out.println("[MORTY] ERROR: Could not remove user with ID " + user.getId());
			e.printStackTrace();
		}
	}
	
	public static void putIntroduction(User user, String messageId) {
		String sql = "UPDATE Users SET Intro = " + messageId + " WHERE UserID = " + user.getId();
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.out.println("[MORTY] ERROR: Could not enter intro for user with ID " + user.getId());
			e.printStackTrace();
		}
	}
	
	public static boolean hasIntroduction(User user) {
		String sql = "SELECT Intro FROM Users WHERE UserID = " + user.getId();
		
		try (Statement stmt  = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			rs.next();
			String intro = rs.getString("Intro");
			stmt.close();
			
			return (intro != "0");
			
		} catch (Exception e) {
			return false;
		}
	}
	
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
	
}
