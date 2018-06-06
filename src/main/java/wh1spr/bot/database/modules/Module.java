package wh1spr.bot.database.modules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import wh1spr.logger.Logger;

public abstract class Module {

	protected Connection conn = null;
	protected JDA jda = null;
	protected Logger log = null;
	private boolean isReady = false;
	
	public Module(Connection c, JDA j, Logger log) {
		this.log = log;
		this.conn = c;
		this.jda = j;
		log.info("Preparing module...");
		if (!prepare()) {
			log.error("Module could not be prepared. Continuing without module...");
			log.shutdown();
			return;
		}
		log.info("Module prepared.");
		log.info("Updating module...");
		if(!update()) {
			log.error("Module failed to update. Continuing without module...");
			log.shutdown();
			return;
		}
		log.info("Module updated.");
		
		isReady = true;
		log.info("Module is ready to rumble!");
	}
	
	public boolean isReady() {
		return isReady;
	}
	
	protected void setReady(boolean b) {
		this.isReady = b;
	}
	
	/**
	 * Prepares frequently used PreparedStatements for the database in that module.
	 */
	protected abstract boolean prepare();
	
	/**
	 * Disregards contents of database and uses JDA to update the data.
	 */
	protected abstract boolean update();
	
	public ResultSet executeQuery(String sql) throws SQLException {
		if (!isReady) return null;
		if (!sql.toUpperCase().contains("SELECT")) {
			return null;
		}
		
		Statement stmt  = conn.createStatement();
		ResultSet rs    = stmt.executeQuery(sql);
		return rs;
	}
	
	public boolean executeUpdate(String sql) throws SQLException {
		if (!isReady) return false;
		if (!sql.toUpperCase().contains("UPDATE")||!sql.toUpperCase().contains("INSERT")) {
			return false;
		}
		
		int rs = -1;
		Statement stmt  = conn.createStatement();
		rs = stmt.executeUpdate(sql);
		if (rs >= 0) return true;
		return false;
	}
	
	// No addUser() because it's easier with member (no checks for guilds)
	/**
	 * Deletes a user from this module's tables.
	 * @param user User to delete
	 * @return true if user was deleted, false if no rows were deleted
	 */
	public abstract boolean deleteUser(User user);
	public abstract boolean addMember(Member member);
	public abstract boolean deleteMember(Member member);
	public abstract boolean addGuild(Guild guild);
	public abstract boolean deleteGuild(Guild guild);
}
