package wh1spr.bot.commands.dev;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.command.Command;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.dummy.Perm;

public class EvalCommand extends Command {

	public EvalCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}
	
	// Owner only because of obvious reasons (it can pretty much do ANYTHING)
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.hasSpec(Perm.OWNER, invoker) && !invoker.getId().equals(Bot.OWNER)) return;
		
		ScriptEngine se = new ScriptEngineManager().getEngineByName("nashorn");
        se.put("jda", jda);
        if (guild != null)
        	se.put("guild", guild);
        se.put("channel", channel);
        
        try {
            channel.sendMessage("Evaluated Successfully:\n```\n"+se.eval(message.getContentRaw().split(" ",2)[1])+" ```").queue();
            success(message);
        } catch(Exception e) {
            channel.sendMessage("An exception was thrown:\n```\n"+e+" ```").queue();
            failure(message);
        }
	}

}
