package wh1spr.bot.database.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.logger.LoggerCache;

/*
 * Introduction and HasIntro tables
 * 
 * Introduction - UserId, GuildId, MessageId
 * HasIntro - GuildId, IntroChannel (id)
 */
public class IntroModule extends Module {

	public IntroModule(Connection c, JDA j) {
		super(c, j, LoggerCache.getLogger("DB-INTRO"));
	}

	@Override
	protected boolean prepare() {
		try {
			userAddStmt = conn.prepareStatement(userAddSql);
			getIntroChannelStmt = conn.prepareStatement(getIntroChannelSql);
			getIntroMessageStmt = conn.prepareStatement(getIntroMessageSql);
			guildAddStmt = conn.prepareStatement(guildAddSql);
			
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
		// TODO Auto-generated method stub
		return false;
	}
	

	/* SQL Statements */
	private static final String userAddSql = "INSERT OR REPLACE INTO Introduction Values(?,?,?)";
	private static final String getIntroChannelSql = "SELECT IntroChannel FROM HasIntro WHERE GuildId = ?";
	private static final String getIntroMessageSql = "SELECT MessageId FROM Introduction WHERE UserId = ? AND GuildId = ?";
	private static final String guildAddSql = "INSERT OR REPLACE INTO HasIntro Values(?,?)";
	
	private static final String userDeleteSql = "DELETE FROM Introduction WHERE UserId = ?";
	private static final String memberDeleteSql = "DELETE FROM Introduction WHERE UserId = ? AND GuildId = ?";
	private static final String guildDeleteSql = "DELETE FROM Introduction WHERE GuildId = ?; DELETE FROM HasIntro WHERE GuildId = ?";
	
	/* PreparedStatements */
	private static PreparedStatement userAddStmt = null;
	private static PreparedStatement getIntroChannelStmt = null;
	private static PreparedStatement getIntroMessageStmt = null;
	private static PreparedStatement guildAddStmt = null;
	
	private static PreparedStatement userDeleteStmt = null;
	private static PreparedStatement memberDeleteStmt = null;
	private static PreparedStatement guildDeleteStmt = null;
	
	
	/***************
	 *  FUNCTIONS  *
	 ***************/
	
	public boolean addUser(String userId, String guildId, String messageId) {
		if (!isReady()) return false;
		int rs = -1;
		try {
			userAddStmt.setString(1, userId);
			userAddStmt.setString(2, guildId);
			userAddStmt.setString(3, messageId);
			rs = userAddStmt.executeUpdate();
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong adding entry UserId = %s, GuildId = %s, MessageId = %s", userId, guildId, messageId));
			return false;
		}
		return rs>0;
	}

	public String getIntroChannelId(String guildId) {
		if (!isReady()) return null;
		try {
			getIntroChannelStmt.setString(1, guildId);
			ResultSet rs = getIntroChannelStmt.executeQuery();
			if(rs.next()) {
				return rs.getString("IntroChannel");
			}
		} catch (SQLException e) {
			log.error(e, String.format("Something went wrong trying get IntroChannelId for GuildId = %s", guildId));
			return null;
		}
		return null;
	}
	
	public boolean hasIntro(String guildId) {
		if (!isReady()) return false;
		return getIntroChannelId(guildId)!=null;
	}
	
	public boolean hasIntro(String userId, String guildId) {
		if (!isReady()) return false;
		return getIntroMessageId(userId, guildId)!=null;
	}
	
	public TextChannel getIntroChannel(String guildId) {
		if (!isReady()) return null;
		return jda.getTextChannelById(getIntroChannelId(guildId));
	}
	
	public String getIntroMessageId(String userId, String guildId) {
		if (!isReady()) return null;
		try {
			getIntroMessageStmt.setString(1, userId);
			getIntroMessageStmt.setString(2, guildId);
			ResultSet rs = getIntroMessageStmt.executeQuery();
			
			if (rs.next()) return rs.getString("MessageId");
		} catch(SQLException e) {
			log.error(e, String.format("Something went wrong trying to get IntroMessageId for UserId = %s, GuildId = %s", userId, guildId));
		}
		return null;
	}
	
	/**
	 * THIS WILL REMOVE THE GUILD FIRST
	 */
	public boolean addGuild(String guildId, String channelId) {
		if (!isReady()) return false;
		int rs = -1;
		try {
			deleteGuild(guildId);
			guildAddStmt.setString(1, guildId);
			guildAddStmt.setString(2, channelId);
			rs = guildAddStmt.executeUpdate();
		} catch(SQLException e) {
			log.error(e, String.format("Something went wrong trying add GuildId = %s, ChannelId = %s", guildId, channelId));
		}
		return rs>0;
	}
	
	public Message getIntroMessage(String userId, String guildId) {
		if (!isReady()) return null;
		if (!hasIntro(guildId)) return null;
		TextChannel intro = getIntroChannel(guildId);
		if (intro==null) return null;
		return intro.getMessageById(getIntroMessageId(userId, guildId)).complete();
	}
	
	/***************
	 *   ALIASES   *
	 ***************/
	
	public boolean hasIntro(Guild guild) {
		return hasIntro(guild.getId());
	}
	
	public String getIntro(Guild guild) {
		return getIntroChannelId(guild.getId());
	}
	
	public TextChannel getIntroChannel(Guild guild) {
		return getIntroChannel(guild.getId());
	}
	
	public String getIntroMessageId(User user, Guild guild) {
		return getIntroMessageId(user.getId(), guild.getId());
	}
	
	public String getIntroMessageId(Member member) {
		return getIntroMessageId(member.getUser(), member.getGuild());
	}
	
	public Message getIntroMessage(User user, Guild guild) {
		return getIntroMessage(user.getId(), guild.getId());
	}
	
	public Message getIntroMessage(Member member) {
		return getIntroMessage(member.getUser(), member.getGuild());
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
		return false;
	}

	@Override
	public boolean deleteMember(Member member) {
		int rs = -1;
		try {
			memberDeleteStmt.setString(1, member.getUser().getId());
			memberDeleteStmt.setString(2, member.getGuild().getId());
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
		return false;
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