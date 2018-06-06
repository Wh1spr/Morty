package wh1spr.bot.database.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.morty.Morty;
import wh1spr.logger.LoggerCache;

/*
 * Maelstrom table
 * Maelstrom - UserId, acceptRules
 */
public class MaelstromModule extends Module {

	public MaelstromModule(Connection c, JDA j) {
		super(c, j, LoggerCache.getLogger("DB-MAELSTROM"));
	}

	@Override
	protected boolean prepare() {
		try {
			userAcceptedStmt = conn.prepareStatement(userAcceptedSql);
			setAcceptStmt = conn.prepareStatement(setAcceptSql);
			
			userAddStmt = conn.prepareStatement(userAddSql);
			userDeleteStmt = conn.prepareStatement(userDeleteSql);
		} catch (SQLException e) {
			log.error(e, "Error while preparing module.");
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean update() {
		// TODO Auto-generated method stub
		return false;
	}
	

	/* SQL Statements */
	private static final String userAcceptedSql = "SELECT acceptRules FROM Maelstrom WHERE UserId = ?";
	private static final String setAcceptSql = "INSERT OR REPLACE INTO Maelstrom Values(?,?)";
	
	private static final String userAddSql = "INSERT OR REPLACE INTO Maelstrom Values(?,?)";
	private static final String userDeleteSql = "DELETE FROM Maelstrom WHERE UserId = ?";
	
	/* PreparedStatements */
	private static PreparedStatement userAcceptedStmt = null;
	private static PreparedStatement setAcceptStmt = null;
	
	private static PreparedStatement userAddStmt = null;
	private static PreparedStatement userDeleteStmt = null;
	
	
	/***************
	 *  FUNCTIONS  *
	 ***************/
	
	public boolean addUser(String userId) {
		if (!isReady()) return false;
		if (jda.getGuildById(Morty.MAELSTROM).getMemberById(userId)==null) return false;
		int rs = -1;
		try {
			userAddStmt.setString(1, userId);
			userAddStmt.setInt(2, 0);
			rs = userAddStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to add user with ID = %s", userId));
			return false;
		}
		return rs>0;
	}
	
	public boolean accepted(String userId) {
		if (!isReady()) return false;
		if (jda.getGuildById(Morty.MAELSTROM).getMemberById(userId)==null) return false;
		try {
			userAcceptedStmt.setString(1, userId);
			userAcceptedStmt.setInt(2, 0);
			ResultSet rs = userAcceptedStmt.executeQuery();
			
			if(rs.next()) {
				if (rs.getInt("acceptRules") == 1) return true;
			}
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to add user with ID = %s", userId));
			return false;
		}
		return false;
	}

	public boolean setAccept(String userId, boolean accept) {
		if (!isReady()) return false;
		if (jda.getGuildById(Morty.MAELSTROM).getMemberById(userId)==null) return false;
		int rs = -1;
		try {
			setAcceptStmt.setString(1, userId);
			setAcceptStmt.setInt(2, accept?1:0);
			rs = userAddStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong set acceptRules property to %s for user with ID = %s", Boolean.toString(accept), userId));
			return false;
		}
		return rs>0;
	}
	
	/***************
	 *   ALIASES   *
	 ***************/
	public boolean addUser(User user) {
		return addUser(user.getId());
	}
	
	public boolean accepted(User user) {
		return accepted(user.getId());
	}
	
	public boolean accepted(Member member) {
		if (!member.getGuild().getId().equals(Morty.MAELSTROM)) return false;
		return accepted(member.getUser());
	}
	
	public boolean setAccept(User user, boolean accept) {
		return setAccept(user.getId(), accept);
	}
	
	/***************
	 *  INHERITED  * - Inherited functions don't need isReady() permissions, as they can be used in update()
	 ***************/

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

	@Override
	public boolean addMember(String memberId) {
		return addUser(memberId);
	}

	@Override
	public boolean deleteMember(String memberId) {
		return deleteUser(memberId);
	}

	@Override
	public boolean addGuild(String guildId) {
		return false;
	}

	@Override
	public boolean deleteGuild(String guildId) {
		return false;
	}

}
