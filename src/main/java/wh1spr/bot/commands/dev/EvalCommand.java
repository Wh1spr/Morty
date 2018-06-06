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
import wh1spr.bot.database.Database2;
import wh1spr.bot.dummy.Bot;
import wh1spr.bot.dummy.Perm;

public class EvalCommand extends Command {

	public EvalCommand(String name, Bot b, String... aliases) {
		super(name, aliases);
		this.b = b;
		this.setMaelstromOnly(false);
	}
	private Bot b = null;
	
	// Owner only because of obvious reasons (it can pretty much do ANYTHING)
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!Perm.hasSpec(Perm.OWNER, invoker) && !invoker.getId().equals(Bot.OWNER)) return;
		
		ScriptEngine se = new ScriptEngineManager().getEngineByName("nashorn");
        se.put("jda", jda);
        if (guild != null)
        	se.put("guild", guild);
        se.put("channel", channel);
        se.put("dbeco", Database2.getEco());
        se.put("dbent", Database2.getEntity());
        se.put("bot", b);
        
        try {
            channel.sendMessage("Evaluated Successfully:\n```\n"+se.eval(message.getContentRaw().split(" ",2)[1]
            		.replaceAll("eco", "Packages.wh1spr.bot.commands.economy.util.EconomyStatus")
            		.replaceAll("main", "Packages.wh1spr.bot.main")
            		.replaceAll("db", "Packages.wh1spr.bot.database.Database2")
            		.replaceAll("perm", "Packages.wh1spr.bot.dummy.Perm")
            		.replaceAll("rank", "Packages.wh1spr.bot.dummy.Rank")
            		.replaceAll("userget", "jda.getUserById"))+" ```").queue();
            success(message);
        } catch(Exception e) {
            channel.sendMessage("An exception was thrown:\n```\n"+e+" ```").queue();
            failure(message);
        }
        
	}

}
