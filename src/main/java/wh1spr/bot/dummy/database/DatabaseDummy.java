package wh1spr.bot.dummy.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.dummy.Bot;
import wh1spr.logger.LoggerCache;

public class DatabaseDummy {

	private static Connection conn = null;
	private Bot bot;
	
	public DatabaseDummy(Bot bot) {
		this.bot = bot;
		if(conn == null) conn = getConn();
		if(newConn) {
			try {
				guildAddStmt = conn.prepareStatement(guildAddSql);
				guildDelStmt = conn.prepareStatement(guildDelSql);
				userAddStmt = conn.prepareStatement(userAddSql);
				userAddStmt2 = conn.prepareStatement(userAddSql2);
				userAddStmt3 = conn.prepareStatement(userAddSql3);
				userDelStmt = conn.prepareStatement(userDelSql);
			} catch (SQLException e) {
				//Shouldnt happen
				bot.getLog().error(e, "Couldn't prepare statements in Database, shutting down...");
				bot.shutdown();
			}
		}
	}
	
	private static boolean newConn = false;
	public Connection getConn() {
		if (conn != null) return conn;
		
		newConn = true;
		
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
	
	public ResultSet executeQuery(String sql) throws SQLException {
		if (!sql.toUpperCase().contains("SELECT")) {
			return null;
		}
		
		Statement stmt  = DatabaseDummy.conn.createStatement();
		ResultSet rs    = stmt.executeQuery(sql);
		return rs;
	}
	
	public boolean executeUpdate(String sql) throws SQLException {
		if (!sql.toUpperCase().contains("UPDATE")||!sql.toUpperCase().contains("INSERT")) {
			return false;
		}
		
		int rs = -1;
		Statement stmt  = DatabaseDummy.conn.createStatement();
		rs = stmt.executeUpdate(sql);
		if (rs >= 0) return true;
		return false;
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
	 * This is now an issue for efficiency only, since we could just replace it multiple times. Also,
	 * since we only have Morty for now, I'm just gonna leave the eventhandler implementation for later.
	 * We let all handlers fire everything all the time.
	 * 
	 * In the future there will be a task list that bots needs to check before: joining a guild, adding a guild to the server,
	 * ... in case they are offline or haven't been online in a while. This will update the db again.
	 * 
	 */
	
	private static PreparedStatement guildAddStmt = null;
	private static PreparedStatement guildDelStmt = null;
	private static final String guildAddSql = "INSERT OR REPLACE INTO Guilds Values(?,?,?,?,?,?)";
	private static final String guildDelSql = "DELETE FROM Guilds WHERE GuildId = ?; DELETE FROM InGuild WHERE GuildId = ?;"
			+ "DELETE FROM Economy_Settings WHERE GuildId = ?; DELETE FROM Economy WHERE GuildId = ?;"
			+ "DELETE FROM HasIntro WHERE GuildId = ?; DELETE FROM Introduction Where GuildId = ?";
	public void addGuild(Guild guild) {
		try {
			guildAddStmt.setString(1, guild.getId());
			guildAddStmt.setString(2, guild.getName());
			guildAddStmt.setString(3, guild.getOwner().getUser().getId());
			guildAddStmt.setInt(4, guild.getMembers().size());
			guildAddStmt.setInt(5, guild.getTextChannels().size());
			guildAddStmt.setInt(6, guild.getVoiceChannels().size());
			
			guildAddStmt.executeUpdate();
			
			List<User> users = new ArrayList<User>();
			guild.getMembers().forEach(e->users.add(e.getUser()));
			addUsers(users);
		} catch (SQLException e) {
			bot.getLog().error(e, "Could not insert guild with ID = " + guild.getId() + ".");
		}
	}
	public void deleteGuild(Guild guild) {
		try {
			guildDelStmt.setString(1, guild.getId());
			guildDelStmt.setString(2, guild.getId());
			guildDelStmt.setString(3, guild.getId());
			guildDelStmt.setString(4, guild.getId());
			guildDelStmt.setString(5, guild.getId());
			guildDelStmt.setString(6, guild.getId());
			guildDelStmt.executeUpdate();
			
		} catch (Exception e) {
			bot.getLog().error(e, "Could not delete guild with ID = " + guild.getId() + ".");
		}
	}
	
	private static PreparedStatement userAddStmt = null; // Users
	private static PreparedStatement userAddStmt2 = null; // InGuild
	private static PreparedStatement userAddStmt3 = null; // economy init balance
	private static PreparedStatement userDelStmt = null;
	private static final String userAddSql = "INSERT OR REPLACE INTO Users Values(?,?)";
	private static final String userAddSql2 = "INSERT OR REPLACE INTO InGuild Values(?,?)";
	private static final String userAddSql3 = "INSERT OR REPLACE INTO Economy Values(?,?,?)";
	private static final String userDelSql = "DELETE FROM Users WHERE UserId = ?; DELETE FROM Economy WHERE UserId = ?;"
			+ "DELETE FROM InGuild WHERE UserId = ?; DELETE FROM Introduction WHERE UserId = ?;";
	public void addUsers(List<User> users) {
		try {
			conn.setAutoCommit(false);
			
			for (User el : users) {
				userAddStmt.setString(1, el.getId());
				userAddStmt.setString(2, el.getName());
				userAddStmt.executeUpdate();
				for (Bot b : Main.getBots()) {
					for (Guild g : b.getJDA().getMutualGuilds(el)) {
						userAddStmt2.setString(1, el.getId());
						userAddStmt2.setString(2, g.getId());
						userAddStmt2.executeUpdate();
						
						ResultSet rs = executeQuery("SELECT (GuildId, EcoStartBal) FROM Economy_Settings WHERE GuildId = " + g.getId());
						if (rs.getFetchSize() > 0) {
							while (rs.next()) {
								double bal = rs.getDouble("EcoStartBal");
								userAddStmt3.setString(1, g.getId());
								userAddStmt3.setString(2, el.getId());
								userAddStmt3.setDouble(3, bal);
								userAddStmt3.executeUpdate();
							}
						}
					}
				}
			}
			
			
			
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			bot.getLog().error(e, "Couldn't insert list of users into db.");
		}
	}
	public void addUser(User user) {
		try {
			userAddStmt.setString(1, user.getId());
			userAddStmt.setString(2, user.getName());
			userAddStmt.executeUpdate();
			
			for (Bot b : Main.getBots()) {
				for (Guild g : b.getJDA().getMutualGuilds(user)) {
					userAddStmt2.setString(1, user.getId());
					userAddStmt2.setString(2, g.getId());
					userAddStmt2.executeUpdate();
					
					ResultSet rs = executeQuery("SELECT (GuildId, EcoStartBal) FROM Economy_Settings WHERE GuildId = " + g.getId());
					if (rs.getFetchSize() > 0) {
						while (rs.next()) {
							double bal = rs.getDouble("EcoStartBal");
							userAddStmt3.setString(1, g.getId());
							userAddStmt3.setString(2, user.getId());
							userAddStmt3.setDouble(3, bal);
							userAddStmt3.executeUpdate();
						}
					}
				}
			}
		} catch (SQLException e) {
			bot.getLog().error(e, "Couldn't insert user with id " + user.getId() + " into db.");
		}
	}
	/*
	 * If the provided user is also an owner of a guild one of our bots is in, also delete that guild.
	 * ! AutoEventHandlers will take care of other active bots leaving that server, inactive bots might readd it.
	 */
	public void deleteUser(User user) {
		if (user.getId().equals(Bot.OWNER)) return; //cant delete myself now can I?
		try {
			userDelStmt.setString(1, user.getId());
			userDelStmt.setString(2, user.getId());
			userDelStmt.setString(3, user.getId());
			userDelStmt.setString(4, user.getId());
			
			userDelStmt.executeUpdate();
			
			try {
				String checkOwner = "SELECT GuildId FROM Guilds WHERE OwnerId = " + user.getId();
				ResultSet rs = executeQuery(checkOwner);
				if (rs.getFetchSize() > 0) {
					while(rs.next()) {
						Guild toRemove = bot.getJDA().getGuildById(rs.getString("GuildId"));
						if (toRemove != null) deleteGuild(toRemove);
					}
				}
			} catch (Exception e) {
				bot.getLog().error("Could not check if user with ID = " + user.getId() + " was owner of a guild."
						+ " If this was the case, an error could occur while getting owner user.");
			}
			
			
		} catch (Exception e) {
			bot.getLog().error(e, "Could not delete user with ID = " + user.getId() + ".");
		}
	}
	
}
