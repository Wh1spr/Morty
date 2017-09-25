package wh1spr.morty;

import java.sql.Connection;
import java.sql.DriverManager;
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
	
}
