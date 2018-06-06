package wh1spr.bot.database.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.logger.LoggerCache;

/* 
 * Users and Guilds tables
 * 
 * Users - UserId, UserName
 * Guilds - GuildId, GuildName, OwnerId, NrOfMembers, NrOfTChannels, NrOfVChannels
 */
public class EntityModule extends Module {

	public EntityModule(Connection c, JDA j) {
		super(c, j, LoggerCache.getLogger("DB-ENT"));
	}

	@Override
	protected boolean prepare() {
		try {
			userAddStmt = conn.prepareStatement(userAddSql);
			userDeleteStmt = conn.prepareStatement(userDeleteSql);
			guildAddStmt = conn.prepareStatement(guildAddSql);
			guildDeleteStmt = conn.prepareStatement(guildDeleteSql);
		} catch (SQLException e) {
			log.error(e, "Error while preparing module.");
			return false;
		}
		return true;
	}

	@Override
	protected boolean update() {
		// TODO Auto-generated method stub
		return true;
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
	
	public void addUser(User user) {
		if (!isReady()) return;
		if (user == null) return;
		try {
			userAddStmt.setString(1, user.getId());
			userAddStmt.setString(2, user.getName());
			
			userAddStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to add user with ID = %s", user.getId()));
			return;
		}
	}
	
	public void addUsers(List<User> users) {
		if (!isReady()) return;
		Iterator<User> iter = users.iterator();
		while(iter.hasNext()) {
			addUser(iter.next());
		}
	}
	
	public void addMembers(List<Member> members) {
		if (!isReady()) return;
		Iterator<Member> iter = members.iterator();
		while(iter.hasNext()) {
			addMember(iter.next());
		}
	}
	
	/***************
	 *   ALIASES   *
	 ***************/
	
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
	public boolean addMember(Member member) {
		int rs = -1;
		try {
			userAddStmt.setString(1, member.getUser().getId());
			userAddStmt.setString(2, member.getUser().getName());
			
			rs = userAddStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to add user with ID = %s", member.getUser().getId()));
			return false;
		}
		return rs>0;
	}
	@Override
	public boolean addMember(String memberId) {
		addUser(jda.getUserById(memberId));
		return true;
	}

	@Override
	public boolean deleteMember(String memberId) {
		return deleteUser(memberId);
	}

	@Override
	public boolean addGuild(Guild guild) {
		int rs = -1;
		try {
			guildAddStmt.setString(1, guild.getId());
			guildAddStmt.setString(2, guild.getName());
			guildAddStmt.setString(3, guild.getOwner().getUser().getId());
			guildAddStmt.setInt(4, guild.getMembers().size());
			guildAddStmt.setInt(5, guild.getTextChannels().size());
			guildAddStmt.setInt(6, guild.getVoiceChannels().size());
			
			rs = guildAddStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to add guild with ID = %s", guild.getId()));
			return false;
		}
		return rs>0;
	}

	@Override
	public boolean addGuild(String guildId) {
		return jda.getGuildById(guildId)==null?null:addGuild(jda.getGuildById(guildId));
	}
	
	@Override
	public boolean deleteGuild(String guildId) {
		int rs = -1;
		try {
			guildDeleteStmt.setString(1, guildId);
			rs = guildDeleteStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying to delete guild with ID = %s", guildId));
			return false;
		}
		return rs>0;
	}

}
