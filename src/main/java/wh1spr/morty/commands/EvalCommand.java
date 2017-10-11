package wh1spr.morty.commands;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.morty.AutoEventHandler;
import wh1spr.morty.Database;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class EvalCommand extends Command {

	public EvalCommand(String name, String... aliases) {
		super(name, aliases);
	}
	
	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker.getUser(), true)) return;
		
		ScriptEngine se = new ScriptEngineManager().getEngineByName("nashorn");
        se.put("jda", jda);
        se.put("guild", guild);
        se.put("channel", channel);
        se.put("imagetimeout", new SendImageCommand(null, null));
        
        try
        {
            channel.sendMessage("Evaluated Successfully:\n```\n"+se.eval(message.getStrippedContent().split(" ",2)[1])+" ```").queue();
            message.addReaction("✅").queue();
        } 
        catch(Exception e)
        {
            channel.sendMessage("An exception was thrown:\n```\n"+e+" ```").queue();
            message.addReaction("❌").queue();
        }
	}

}
