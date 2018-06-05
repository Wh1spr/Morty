package wh1spr.bot.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Main;
import wh1spr.bot.Tools;
import wh1spr.bot.commands.economy.util.Balance;
import wh1spr.bot.database.modules.*;
import wh1spr.bot.dummy.Bot;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;

// Since we can get the connection statically, statements that will be executed rarely are not put in here.
public class Database2 {

	private static Logger log = LoggerCache.getLogger("DATABASE");
	
	private static Connection conn = null;
	private static JDA jda = null;
	private static boolean isReady = false;
	private static ArrayList<Module> modules = null;
	
	public static boolean start(JDA jda) {
		log.info("Getting connection to DB");
		conn = getConn();
		Database2.jda = jda;
		//instance modules with conn, jda
		modules = new ArrayList<Module>();
		eco = new EconomyModule(conn, jda);
		if(eco.isReady()) modules.add(eco);
		
		//getters setters
		log.info("Database is ready to rumble!");
		isReady = true;
		return isReady;
	}
	
	public static boolean isReady() {
		return isReady;
	}
	
	private static EconomyModule eco = null;
	
	public static EconomyModule getEco() {
		return Database2.eco;
	}
	
	private Bot bot;
	
	public Database2(Bot bot) {
		this.bot = bot;
		if(conn == null) conn = getConn();
		prepare();
	}
	
	public static Connection getConn() {
		Connection con = null;
		
		String url = "jdbc:sqlite:data/Morty2.db";
		try {
			Class.forName("org.sqlite.JDBC");
			if(!Files.exists(Paths.get("data/Morty2.db"))) {
				log.error("Morty2 Database is not present @data/Morty2.db and application cannot run. Exiting...");
				LoggerCache.shutdown();
				System.exit(1);
			}
			con = DriverManager.getConnection(url);
			log.info("Connection to the database has been established.");
		} catch (Exception e) {
			log.error(e, "Could not establish connection to the Database. Exiting...");
			LoggerCache.shutdown();
			System.exit(1);
		}
		return con;
	}
	
	private static void prepare() {
		try {
			guildAddStmt = conn.prepareStatement(guildAddSql);
			guildDelStmt = conn.prepareStatement(guildDelSql);
			userAddStmt  = conn.prepareStatement(userAddSql);
			userAddStmt2 = conn.prepareStatement(userAddSql2);
			userAddStmt3 = conn.prepareStatement(userAddSql3);
			userDelStmt  = conn.prepareStatement(userDelSql);
			
			balanceUpdateStmt = conn.prepareStatement(balanceUpdateSql);
			
			ecoSetupStmt = conn.prepareStatement(ecoSetupSql);
		} catch (SQLException e) {
			//Shouldnt happen, but does sometimes whoops
			log.error(e, "Couldn't prepare statements in Database, shutting down...");
			//cases here for reset() call
			if (!Main.getBots().isEmpty())
				Collections.unmodifiableCollection(Main.getBots()).forEach(bot->bot.shutdown());
			else {
				LoggerCache.shutdown();
				System.exit(1);
			}
		}
	}
	
	public ResultSet executeQuery(String sql) throws SQLException {
		if (!sql.toUpperCase().contains("SELECT")) {
			return null;
		}
		
		Statement stmt  = Database2.conn.createStatement();
		ResultSet rs    = stmt.executeQuery(sql);
		return rs;
	}
	
	public boolean executeUpdate(String sql) throws SQLException {
		if (!sql.toUpperCase().contains("UPDATE")||!sql.toUpperCase().contains("INSERT")) {
			return false;
		}
		
		int rs = -1;
		Statement stmt  = Database2.conn.createStatement();
		rs = stmt.executeUpdate(sql);
		if (rs >= 0) return true;
		return false;
	}
	
	/*
	 * What follows will be queries that can be used by all the bots.
	 * These include new users and new guilds, removing them and updating them.
	 * Because we can have multiple bots in the same guild, we have to somehow let the 
	 * eventhandlers know when to fire an update so we dont flood the database with duplicates.
	 * 
	 * A solution could be to have Main hold a list of guilds and their bots, and have ONLY THE FIRST BOT to be added,
	 * that is also in that guild, fire the update.
	 * 
	 * This is now an issue for efficiency only, since we could just replace it multiple times. Also,
	 * since we only have Morty for now, I'm just gonna leave the eventhandler implementation for later.
	 * We let all handlers fire everything, all the time.
	 * 
	 * In the future there will be a task list that bots needs to check before: joining a guild, adding a guild to the server,
	 * ... in case they are offline or haven't been online in a while. This will update the db again.
	 * mainly so a banned guild for example wouldn't be added, or when joining a banned guild we leave immediately.
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
	private static final String userAddSql = "INSERT OR REPLACE INTO Users Values(?,?,?)";
	private static final String userAddSql2 = "INSERT OR REPLACE INTO InGuild Values(?,?)";
	private static final String userAddSql3 = "INSERT OR REPLACE INTO Economy Values(?,?,?)";
	private static final String userDelSql = "DELETE FROM Users WHERE UserId = ?; DELETE FROM Economy WHERE UserId = ?;"
			+ "DELETE FROM InGuild WHERE UserId = ?; DELETE FROM Introduction WHERE UserId = ?;";
	public void addUsers(List<User> users) {
		try {
			for (User el : users) {
				if(el.isBot()) continue;
				userAddStmt.setString(1, el.getId());
				userAddStmt.setString(2, el.getName());
				userAddStmt.setString(3, el.getName()+'#'+el.getDiscriminator());
				userAddStmt.executeUpdate();
				for (Bot b : Main.getBots()) {
					JDA jda = b.getJDA();
					if (jda == null) jda = el.getJDA();
					for (Guild g : jda.getMutualGuilds(el)) {
						userAddStmt2.setString(1, el.getId());
						userAddStmt2.setString(2, g.getId());
						userAddStmt2.executeUpdate();
						
						ResultSet rs = executeQuery("SELECT GuildId, StartBal FROM Economy_Settings WHERE GuildId = '" + g.getId() + "'");
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
		} catch (SQLException e) {
			bot.getLog().error(e, "Couldn't insert list of users into db.");
		}
	}
	public void addUser(User user) {
		if (user.isBot()) return;
		try {
			userAddStmt.setString(1, user.getId());
			userAddStmt.setString(2, user.getName());
			userAddStmt.setString(3, user.getName()+'#'+user.getDiscriminator());
			userAddStmt.executeUpdate();
			
			for (Bot b : Main.getBots()) {
				JDA jda = b.getJDA();
				if (jda == null) jda = user.getJDA();
				for (Guild g : jda.getMutualGuilds(user)) {
					userAddStmt2.setString(1, user.getId());
					userAddStmt2.setString(2, g.getId());
					userAddStmt2.executeUpdate();
					
					ResultSet rs = executeQuery("SELECT GuildId, EcoStartBal FROM Economy_Settings WHERE GuildId = '" + g.getId() + "'");
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
	 * ! AutoEventHandlers will take care of other active bots leaving that server, inactive bots might read it.
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
				String checkOwner = "SELECT GuildId FROM Guilds WHERE OwnerId = '" + user.getId() + "'";
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
	
	private static PreparedStatement balanceUpdateStmt = null;
	private static final String balanceUpdateSql = "INSERT OR REPLACE INTO Economy Values(?,?,?)";
	@Deprecated
	public void updateBal(User user, Guild guild, double val) {
		try {
			balanceUpdateStmt.setString(1, guild.getId());
			balanceUpdateStmt.setString(2, user.getId());
			balanceUpdateStmt.setDouble(3, Tools.round(val));
			
			balanceUpdateStmt.executeUpdate();
		} catch (Exception e) {
			bot.getLog().error(e, String.format("Could not update balance. UserId = %s, new Balance = %.2f", user.getId(), val));
		}
	}
	@Deprecated
	public void updateBal(Member member, double val) {
		updateBal(member.getUser(), member.getGuild(), val);
	}
	
	@Deprecated
	public void updateBalances(Collection<Balance> bals) {
		bot.getLog().info(String.format("Updating %d balance values.", bals.size()));
		Iterator<Balance> iter = bals.iterator();
		while(iter.hasNext()) {
			Balance next = iter.next();
			updateBal(next.getMember(), next.getBal());
		}
	}
	
	private static final String balancesGetSql = "SELECT * FROM Economy";
	/**
	 * Retrieve balances from database.
	 * <b>Note: These balances come from the db, which gets updated automatically once every ten minutes since startup, or with the {@link UpdateBalanceCommand}</b>
	 * @return A set of balances found in the database
	 */
	@Deprecated
	public Set<Balance> getBalances() {
		try {
			Set<Balance> s = new HashSet<Balance>();
			ResultSet rs = executeQuery(balancesGetSql);
			
			while(rs.next()) {
				s.add(new Balance(rs.getDouble("Balance"), rs.getString("userId"), rs.getString("guildId")));
			}
			return s;
		} catch (Exception e) {
			bot.getLog().error("Balances could not be retreived from the DB.");
			return null;
		}
	}
	
	private static final String hasEcoSql = "SELECT COUNT(*) as total FROM Economy_Settings Where GuildId = '%s'";
	@Deprecated
	public boolean hasEconomy(String guildid) {
		try {
			ResultSet rs = executeQuery(String.format(hasEcoSql, guildid));
			rs.next();
			return rs.getInt("total")==1?true:false;
		} catch (SQLException e) {
			bot.getLog().error(e, "Couldn't check if guild with ID " + guildid + " had a set up economy.");
		}
		return false;
	}
	private static final String getEcoInfoSql = "SELECT * FROM Economy_Settings WHERE GuildId = '%s'";
	@Deprecated
	public EcoInfo getGuildInfo(String guildid) {
		if (!hasEconomy(guildid)) return null;
		try {
			ResultSet rs = executeQuery(String.format(getEcoInfoSql, guildid));
			rs.next();
			return new EcoInfo(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getDouble(6),rs.getDouble(7));
		} catch (SQLException e) {
			bot.getLog().error(e, "Couldn't check if guild with ID " + guildid + " had a set up economy.");
		}
		return null;
	}
	private static final String ecoSetupSql = "INSERT OR REPLACE INTO Economy_Settings Values(?,?,?,?,?,?,?)";
	private static PreparedStatement ecoSetupStmt = null; 
	@Deprecated
	public boolean setupEconomy(String guildId, String majSing, String majMult, String minSing, String minMult, Double start, Double daily) {
		try {
			ecoSetupStmt.setString(1, guildId);
			ecoSetupStmt.setString(2, majSing);
			ecoSetupStmt.setString(3, majMult);
			ecoSetupStmt.setString(4, minSing);
			ecoSetupStmt.setString(5, minMult);
			ecoSetupStmt.setDouble(6, Tools.round(start));
			ecoSetupStmt.setDouble(7, Tools.round(daily));
			
			ecoSetupStmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			bot.getLog().error(e, "Could not set up economy for guild with ID " + guildId);
			return false;
		}
	}
}
