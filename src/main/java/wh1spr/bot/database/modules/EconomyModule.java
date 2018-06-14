package wh1spr.bot.database.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.commands.economy.util.Balance;
import wh1spr.bot.database.EcoInfo;
import wh1spr.logger.LoggerCache;

// Economy and Economy_Settings tables.
public class EconomyModule extends Module {

	public EconomyModule(Connection c, JDA j) {
		super(c, j, LoggerCache.getLogger("DB-ECO"));
	}

	@Override
	protected boolean prepare() {
		try {
			balanceUpdateStmt = conn.prepareStatement(balanceUpdateSql);
			hasEcoStmt = conn.prepareStatement(hasEcoSql);
			ecoSetupStmt = conn.prepareStatement(ecoSetupSql);
			getEcoInfoStmt = conn.prepareStatement(getEcoInfoSql);
			
			userDeleteStmt = conn.prepareStatement(userDeleteSql);
			memberDeleteStmt = conn.prepareStatement(memberDeleteSql);
			guildDeleteStmt = conn.prepareStatement(guildDeleteSql);
		} catch (SQLException e) {
			log.error(e, "Error while preparing module.");
			return false;
		}
		return true;
	}

	@Override
	protected boolean update() {
		// TODO
		return true;
	}
	
	/* SQL Statements */
	private static final String balanceUpdateSql = "INSERT OR REPLACE INTO Economy Values(?,?,?)";
	private static final String balancesGetSql = "SELECT * FROM Economy";
	private static final String hasEcoSql = "SELECT COUNT(*) as total FROM Economy_Settings Where GuildId = ?";
	private static final String getEcoInfoSql = "SELECT * FROM Economy_Settings WHERE GuildId = ?";
	private static final String ecoSetupSql = "INSERT OR REPLACE INTO Economy_Settings Values(?,?,?,?,?,?,?)";
	
	private static final String userDeleteSql = "DELETE FROM Economy WHERE UserId = ?";
	private static final String memberDeleteSql = "DELETE FROM Economy WHERE GuildId = ? AND UserId = ?";
	private static final String guildDeleteSql = "DELETE FROM Economy WHERE GuildId = ?; DELETE * FROM Economy_Settings WHERE GuildId = ?";
	
	/* PreparedStatements */
	private static PreparedStatement balanceUpdateStmt = null;
	private static PreparedStatement hasEcoStmt = null;
	private static PreparedStatement getEcoInfoStmt = null;
	private static PreparedStatement ecoSetupStmt = null; 
	
	private static PreparedStatement userDeleteStmt = null;
	private static PreparedStatement memberDeleteStmt = null;
	private static PreparedStatement guildDeleteStmt = null;
	
	
	/***************
	 *  FUNCTIONS  *
	 ***************/
	
	/**
	 * Updates Balance in the database for the given user in the given guild with the given value.
	 * @param user The user to update the balance for.
	 * @param guild The guild to update the balance for.
	 * @param val The new value of the balance.
	 */
	public void updateBal(User user, Guild guild, double val) {
		if (!isReady()) return;
		if (!hasEconomy(guild.getId())) return;
		try {
			balanceUpdateStmt.setString(1, guild.getId());
			balanceUpdateStmt.setString(2, user.getId());
			balanceUpdateStmt.setDouble(3, Tools.round(val));
			balanceUpdateStmt.executeUpdate();
		} catch (Exception e) {
			log.error(e, String.format("Could not update balance. UserId = %s, new Balance = %.2f", user.getId(), val));
		}
	}
	
	/**
	 * Retrieve all balances from database.
	 * <b>Note: These balances come from the db, which gets updated automatically once every ten minutes since startup, or with the {@link wh1spr.bot.commands.dev.FlushEcoCommand}</b>
	 * @return A set of balances found in the database
	 */
	public Set<Balance> getBalances() {
		if (!isReady()) return null;
		try {
			Set<Balance> s = new HashSet<Balance>();
			ResultSet rs = executeQuery(balancesGetSql);
			while(rs.next()) {
				Balance b = new Balance(rs.getDouble("Balance"), rs.getString("userId"), rs.getString("guildId"));
				if (b.getMember()!=null) {
					s.add(b);
				}
			}
			return s;
		} catch (Exception e) {
			log.error(e, "Balances could not be retreived from the DB.");
			return null;
		}
	}
	
	/**
	 * Checks wether or not a guild with id guildid has a set up economy.
	 * @param guildid The ID of the guild to check.
	 * @return true if the guild has a set up economy, false when it hasn't.
	 */
	public boolean hasEconomy(String guildid) {
		if (!isReady()) return false;
		try {
			hasEcoStmt.setString(1, guildid);
			ResultSet rs = hasEcoStmt.executeQuery();
			rs.next();
			return rs.getInt("total")==1?true:false;
		} catch (SQLException e) {
			log.error(e, "Couldn't check if guild with ID " + guildid + " had a set up economy.");
		}
		return false;
	}
	
	
	public EcoInfo getGuildInfo(String guildid) {
		if (!isReady()) return null;
		if (!hasEconomy(guildid)) return null;
		try {
			getEcoInfoStmt.setString(1, guildid);
			ResultSet rs = getEcoInfoStmt.executeQuery();
			rs.next();
			return new EcoInfo(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getDouble(6),rs.getDouble(7));
		} catch (SQLException e) {
			log.error(e, "Couldn't check if guild with ID " + guildid + " had a set up economy.");
		}
		return null;
	}
	
	public boolean setupEconomy(String guildId, String majSing, String majMult, String minSing, String minMult, Double start, Double daily) {
		if (!isReady()) return false;
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
			log.error(e, "Could not set up economy for guild with ID " + guildId);
			return false;
		}
	}
	
	/***************
	 *   ALIASES   *
	 ***************/
	public void updateBal(Member member, double val) {
		updateBal(member.getUser(), member.getGuild(), val);
	}
	public void updateBalances(Collection<Balance> bals) {
		if (!isReady()) return;
		log.info(String.format("Updating %d balance values.", bals.size()));
		Iterator<Balance> iter = bals.iterator();
		while(iter.hasNext()) {
			Balance next = iter.next();
			updateBal(next.getMember(), next.getBal());
		}
	}
	public boolean hasEconomy(Guild guild) {
		return hasEconomy(guild.getId());
	}
	
	/***************
	 *  INHERITED  * - Inherited functions don't need isReady() permissions, as they can be used in update()
	 ***************/
	
	/**
	 * Deletes the given user from the database without checking anything.
	 * @param user The user to be deleted.
	 */
	@Override
	public boolean deleteUser(String userId) {
		int rs = -1;
		try {
			userDeleteStmt.setString(1, userId);
			rs = userDeleteStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to delete user with ID = %s", userId));
			return false;
		}
		return rs>0;
	}
	
	// I'm assuming this member wasn't in the server, and EconomyStatus needs to wait on DB anyways
	@Override
	public boolean addMember(Member member) {
		if (!hasEconomy(member.getGuild())) return false;
		// Guild has an economy, so ei != null
		EcoInfo ei = getGuildInfo(member.getGuild().getId());
		boolean was = isReady();
		this.setReady(true);
		updateBal(member, ei.getStartVal());
		this.setReady(was);
		return true;
	}
	
	@Override
	public boolean addMember(String memberId) {
		return false; // impossible without guild
	}
	
	@Override
	public boolean deleteMember(Member member) {
		int rs = -1;
		try {
			memberDeleteStmt.setString(1, member.getGuild().getId());
			memberDeleteStmt.setString(2, member.getUser() .getId());
			rs = memberDeleteStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to delete member with ID = %s and GuildID = %s",
					member.getUser().getId(), member.getGuild().getId()));
			return false;
		}
		return rs>0;
	}
	
	@Override
	public boolean deleteMember(String memberId) {
		return false; //not safe
	}
	
	@Override
	public boolean addGuild(String guildId) {
		// guild cannot be added, it has to go through EcoSetupCommand
		return false;
	}
	
	@Override
	public boolean deleteGuild(String guildId) {
		int rs = -1;
		try {
			guildDeleteStmt.setString(1, guildId);
			guildDeleteStmt.setString(2, guildId);
			rs = guildDeleteStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to delete guild with ID = %s", guildId));
			return false;
		}
		
		return rs>0;
	}
	
}
