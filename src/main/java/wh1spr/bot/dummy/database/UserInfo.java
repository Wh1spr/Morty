package wh1spr.bot.dummy.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.dummy.Bot;

public final class UserInfo {
	// All data we have on this user in the database (also queries stuff etc). Can not be overridden.
	// Can be used as piece of info in others, but can not be extended as that could lead to confusion when
	// there are multiple UserInfo type files. (Good example would be economy related information.
	
	private boolean updating = true;
	
	private final Bot bot;
	private final User user;
	
	private final Database2 db;
	
	public UserInfo(User user, Bot bot) {
		this.bot = bot;
		this.user = user;
		this.db = new Database2(bot);
		this.updating = true;
	}
	
	public UserInfo(String userId, Bot bot) {
		this(bot.getJDA().getUserById(userId), bot);
	}
	
	public UserInfo(User user, Bot bot, boolean updating) {
		this(user, bot);
		this.updating = updating;
	}
	
	public UserInfo(String userId, Bot bot, boolean updating) {
		this(bot.getJDA().getUserById(userId), bot);
		this.updating = updating;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public boolean isUpdating() {
		return updating;
	}
	
	private List<Guild> guilds = null;
	/**
	 * Returns a list of mutual guilds of the given bot and the user in the database.
	 * @return List of mutual guilds in the database.
	 * @return Empty List if an error occurred.
	 */
	public List<Guild> getGuilds() {
		if (!updating && guilds != null) return guilds;
		List<Guild> list = new ArrayList<Guild>();
		
		getGuildIds().forEach(el->{
			Guild g = bot.getJDA().getGuildById(el);
			if (g != null) list.add(g);
		});
		this.guilds = list;
		return list;
	}
	
	private List<String> guildIds = null;
	/**
	 * Returns a list of IDs of guilds that the user is member of, that are in the database.
	 * A returned ID means that "A bot" has a mutual guild with this user.
	 * @return List of String IDs of guilds that the user is a member of.
	 */
	public List<String> getGuildIds() {
		if (!updating && guildIds != null) return guildIds;
		List<String> list = new ArrayList<String>();
		try {
			ResultSet rs = db.executeQuery("SELECT FROM InGuild WHERE UserId = " + getUser().getId());
			String id = "";
			while(rs.next()) {
				id = rs.getString("GuildId");
				list.add(id);
			}
			this.guildIds = list;
			return list;
		} catch (SQLException e) {
			bot.getLog().error("Could not get guildIds from user with ID " + getUser().getId());
			e.printStackTrace();
		}
		list.clear();
		this.guildIds = list;
		return list;
	}
	
	/**
	 * Returns wether or not the user is in the guild with the given ID.
	 * Note that the the guild has to be a guild in the database.
	 * @param guildId The id of the guild to be checked.
	 */
	public boolean isInGuild(String guildId) {
		return getGuildIds().contains(guildId);
	}
	
	/**
	 * Returns wether or not the user is in the given guild.
	 * Note that the the guild has to be a guild in the database.
	 * @param guild The Guild object to be checked.
	 */
	public boolean isInGuild(Guild guild) {
		return isInGuild(guild.getId());
	}
	
}
