package wh1spr.bot.command;

import java.sql.ResultSet;
import java.sql.Statement;

import wh1spr.bot.commands.SendImageCommand;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.morty.Database;

public class ImageRegistry extends CommandRegistry {
	
	public ImageRegistry(Bot bot) {
		super(bot);
	}

	@Override
	public void removeCommand(String URLorName) {
        if (registry.containsKey(URLorName)) {
        	//string is name
        	registry.remove(URLorName);
        } 
    }
	
	public void registerAllCommands() {
		String sql = "SELECT Name, URL FROM Images";
		
		try (Statement stmt  = Database.conn.createStatement();
		     ResultSet rs    = stmt.executeQuery(sql)){
			while(rs.next()) {
				registerCommand(new SendImageCommand(rs.getString("URL"), rs.getString("Name")));
			}
			
		} catch (Exception e) {
			System.out.println("Could not load images. Exiting...");
			e.printStackTrace();
			System.exit(0);
		}
	}
}
