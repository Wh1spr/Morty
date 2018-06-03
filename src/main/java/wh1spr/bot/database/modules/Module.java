package wh1spr.bot.database.modules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.dv8tion.jda.core.JDA;
import wh1spr.bot.database.Database2;
import wh1spr.logger.Logger;

public abstract class Module {

	private Connection conn = null;
	protected JDA jda = null;
	protected Logger log = null;
	private boolean isReady = false;
	
	public Module(Connection c, JDA j) {
		this.conn = c;
		this.jda = j;
		
		prepare();
		
		isReady = true;
	}
	
	public boolean isReady() {
		return isReady();
	}
	
	protected void setReady(boolean b) {
		this.isReady = b;
	}
	
	/**
	 * Prepares frequently used PreparedStatements for the database in that module.
	 */
	protected abstract void prepare();
	
	/**
	 * Disregards contents of database and uses JDA to update the data.
	 */
	protected abstract void update();
	
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
}
