package wh1spr.bot.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.dv8tion.jda.core.JDA;
import wh1spr.bot.database.modules.*;
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
		ent = new EntityModule(conn, jda);
		if(ent.isReady()) modules.add(ent);
		
		log.info("Database is ready to rumble!");
		isReady = true;
		return isReady;
	}
	
	public static boolean isReady() {
		return isReady;
	}
	
	private static EconomyModule eco = null;
	private static EntityModule  ent = null;
	
	public static EconomyModule getEco() {
		return Database2.eco;
	}
	
	public static EntityModule getEntity() {
		return Database2.ent;
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
	
	public static boolean executeUpdate(String sql) throws SQLException {
		if (!sql.toUpperCase().contains("UPDATE")||!sql.toUpperCase().contains("INSERT")) {
			return false;
		}
		
		int rs = -1;
		Statement stmt  = Database2.conn.createStatement();
		rs = stmt.executeUpdate(sql);
		if (rs >= 0) return true;
		return false;
	}
}
