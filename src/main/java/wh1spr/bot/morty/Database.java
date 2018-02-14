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

// TO BE REDONE
public class Database {
	
	public static Connection conn = null;

	public static void start() {
		String url = "jdbc:sqlite:Morty.db";
		try {
			Class.forName("org.sqlite.JDBC");
			if(!Files.exists(Paths.get("Morty.db"))) {
				Morty.logFatal("Database is not present. Exiting...");
			}
			conn = DriverManager.getConnection(url);
			Morty.logInfo("Connection to Database has been established");
		} catch (Exception e) {
			Morty.logFatal("Could not establish connection to the Database. Exiting...", e);
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
	
	public static boolean exists(User user) {
		String sql = "SELECT COUNT(*) AS total FROM Users WHERE UserID = " + user.getId();
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
	
	public static void removeIntroduction(User user) {
		String sql = "UPDATE Users SET Intro = 0 WHERE UserID = " + user.getId();
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			if (hasIntroduction(user)) throw new SQLException();
		} catch (Exception e) {
			System.out.println("[MORTY] ERROR: Could not remove intro for user with ID " + user.getId());
			e.printStackTrace();
		}
	}
	
	public static void putIntroduction(User user, Message message) {
		String sql = "UPDATE Users SET Intro = " + message.getId() + " WHERE UserID = " + user.getId();
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
			return !intro.equals("0");
			
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
	
	public static String getUserIdByIntroMsgId(String introMsgId) {
		if (introMsgId.contains(" ")) return null;
		String sql = "SELECT UserID FROM Users WHERE Intro = " + introMsgId;
		
		try (Statement stmt  = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			rs.next();
			String intro = rs.getString("UserID");
			stmt.close();
			
			return intro;
			
		} catch (Exception e) {
			return "0";
		}
	}
	
	public static String getNameByUser(User user) {
		String sql = "SELECT Name FROM Users WHERE UserID = " + user.getId();
		
		try (Statement stmt  = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			rs.next();
			String name = rs.getString("Name");
			stmt.close();
			
			return name;
			
		} catch (Exception e) {
			return "NOTFOUND";
		}
	}
	
	public static void updateNameFromUser(User user) {
		String sql = "UPDATE Users SET Name = '" + user.getName() + "' WHERE UserID = " + user.getId();
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.out.println("[MORTY] ERROR: Could not update Name for user with ID " + user.getId());
			e.printStackTrace();
		}
	}
	
	public static void updateNames(JDA jda) {
		String sql = "SELECT UserID FROM Users";
		
		try (Statement stmt  = Database.conn.createStatement();
			 Statement stmtUpd = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			while (rs.next()) {
				String id = rs.getString("UserID");
				User user = jda.getUserById(id);
				if (user == null) {
					stmtUpd.executeUpdate("DELETE FROM Users WHERE UserID = '" + id + "'");
				} else if (jda.getGuildById(C.GUILD).getMember(user) == null) {
					remove(user);
				} else {
					stmtUpd.executeUpdate("UPDATE Users SET Name = '" + user.getName() + "' WHERE UserID = " + id);
				}	
			}
			stmt.close();
		} catch (Exception e) { //Shouldnt happen...
			e.printStackTrace();
		}
	}
	
	public static boolean removeImage(String URLorName) {
		String sql = "DELETE FROM Images WHERE Name = '" + URLorName + "' OR URL = '" + URLorName + "'";
		
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (Exception e) {
			System.out.println("[MORTY] ERROR: Could not remove Image.");
			Morty.logError(e.getMessage());
			return false;
		}
	}
	
	// True if it worked, false if there already is one with this name
	public static boolean putImage(String URL, String name) {
		if (existsImage(name)) return false; //In this way, it IS possible to have aliases
		
		String sql = "INSERT INTO Images(URL, Name) VALUES('" + URL + "','" + name + "')";
		
		try {
			Statement stmt = Database.conn.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			return true;
		} catch (Exception e) {
			System.out.println("[MORTY] ERROR: Could not insert new Image");
			Morty.logError(e.getMessage());
			return false;
		}
	}
	
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
	
	// All tests are executed using C.OWNER id, since it's always in the database.
	public static void test() {
		Morty.logInfo("Testing Database...");
		Morty.logInfo("");
		
		User Owner = Morty.getJDA().getUserById(C.OWNER);
		
		boolean UserIDfetch = exists(Owner);
		boolean hasPermSpecificOwner = Permission.hasPerm(Permission.OWNER, Owner, true);
		boolean hasPermSpecificBot = Permission.hasPerm(Permission.BOT, Owner, true);
		boolean hasPermNonSpecificBot = Permission.hasPerm(Permission.BOT, Owner, false);
		
		String introId = Database.getIntroductionId(Owner);
		
		Morty.logInfo("UserID's - " + UserIDfetch);
		Morty.logInfo("Has permission, specific: Owner - " + hasPermSpecificOwner);
		Morty.logInfo("Has permission, specific: Bot - " + hasPermSpecificBot);
		Morty.logInfo("Has permission, non-specific: Bot - " + hasPermNonSpecificBot);
		Morty.logInfo("Introduction ID - " + introId);
		Morty.logInfo("Setting IntroductionId to '0'..."); Database.removeIntroduction(Owner);
		Morty.logInfo("Introduction ID - " + Database.getIntroductionId(Owner));
		Morty.logInfo("Setting IntroductionId to '" + introId + "'...");
		Morty.logInfo("Has introduction, owner: " + Database.hasIntroduction(Owner));
		Morty.logInfo("");
		Morty.logInfo("End of tests.");
		
	}
	
}
