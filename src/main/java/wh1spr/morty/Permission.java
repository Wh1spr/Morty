package wh1spr.morty;

import java.sql.ResultSet;
import java.sql.Statement;

import net.dv8tion.jda.core.entities.User;

public class Permission {

	public static final int GUEST = 1;
	public static final int MEMBER = 2;
	public static final int GRADUATED = 3;
	public static final int BOT = 4;
	public static final int WINA = 5;
	public static final int GOLDEN_BOT = 6;
	public static final int ADMIN = 7;
	public static final int OWNER = 9;
	
	public static boolean hasPerm(int perm, User user, boolean specific) {
//		return true;
		String sql = "SELECT Perms FROM Users WHERE UserID = " + user.getId();
		
		if (!Database.exists(user)) return false;
		
		try (Statement stmt  = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			rs.next();
			String perms = rs.getString("Perms");
			stmt.close();
			
			if (specific) {
				return perms.contains(String.valueOf(perm));
			} else {
				for (int i = perm; i < 10; i++) {
					if (perms.contains(String.valueOf(i))) return true;
				}
				return false;
			}
			
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void givePerm(int perm, User user) {
		String sql;
		if (!hasPerm(perm, user, true)) {
			if (!Database.exists(user)) {
				sql = "INSERT INTO Users(UserID, Perms) VALUES('" + user.getId() + "','" + perm + "')"; 
			} else {
				sql = "UPDATE Users SET Perms = " + getPerms(user) + perm + " WHERE UserID = " + user.getId();
			}
			try {
				Statement stmt = Database.conn.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
			} catch (Exception e) {
				System.out.println("[MORTY] ERROR: Could not give perm to user with ID " + user.getId());
				e.printStackTrace();
			}
		}
	}
	
	public static String getPerms(User user) {
		if (Database.exists(user)) {
			String sql = "SELECT Perms FROM Users WHERE UserID = " + user.getId();
			
			try (Statement stmt  = Database.conn.createStatement();
			     ResultSet rs    = stmt.executeQuery(sql)){
				rs.next();
				String result = rs.getString("Perms");
				stmt.close();
				return result;
				
			} catch (Exception e) {
				return "";
			}
		} return "";
	}
	
	public static void removePerm(int perm, User user) {
		String sql;
		if (hasPerm(perm, user, true)) {
			sql = "UPDATE Users SET Perms = '" + getPerms(user).replaceFirst(String.valueOf(perm), "") + "' WHERE UserID = " + user.getId();
			try {
				Statement stmt = Database.conn.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
			} catch (Exception e) {
				System.out.println("[MORTY] ERROR: Could not remove perm from user with ID " + user.getId());
				e.printStackTrace();
			}
		}
	}
}
