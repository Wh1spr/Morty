package wh1spr.bot.database.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.logger.LoggerCache;

public class ImageModule extends Module {

	public ImageModule(Connection c, JDA j) {
		super(c, j, LoggerCache.getLogger("DB-IMG"));
	}

	@Override
	protected boolean prepare() {
		try {
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
	private static final String userAddSql = "INSERT OR REPLACE INTO Users Values(?,?)";
	private static final String userDeleteSql = "DELETE FROM Users WHERE UserId = ?";
	private static final String guildAddSql = "INSERT OR REPLACE INTO Guilds Values(?,?,?,?,?,?)";
	private static final String guildDeleteSql = "DELETE FROM Guilds WHERE GuildId = ?";
	
	/* PreparedStatements */
	private static PreparedStatement userAddStmt = null;
	private static PreparedStatement userDeleteStmt = null;
	private static PreparedStatement guildAddStmt = null;
	private static PreparedStatement guildDeleteStmt = null;
	
	
	/***************
	 *  FUNCTIONS  *
	 ***************/

	/***************
	 *   ALIASES   *
	 ***************/
	
	/***************
	 *  INHERITED  * - Inherited functions don't need isReady() permissions, as they can be used in update()
	 ***************/

	@Override
	public boolean deleteUser(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addMember(String memberId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteMember(String memberId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addGuild(String guildId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteGuild(String guildId) {
		// TODO Auto-generated method stub
		return false;
	}

}

