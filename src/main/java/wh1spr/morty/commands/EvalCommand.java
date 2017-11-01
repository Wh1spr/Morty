package wh1spr.morty.commands;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.vdurmont.emoji.EmojiManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import wh1spr.morty.Permission;
import wh1spr.morty.command.Command;

public class EvalCommand extends Command {

	public EvalCommand(String name, String... aliases) {
		super(name, aliases);
	}
	
	// Owner only because of obvious reasons (it can pretty much do ANYTHING)
	@Override
	public void onCall(JDA jda, Guild guild, TextChannel channel, Member invoker, Message message, List<String> args) {
		if (!Permission.hasPerm(Permission.OWNER, invoker.getUser(), true)) {
			message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
			return;
		}
		
		ScriptEngine se = new ScriptEngineManager().getEngineByName("nashorn");
        se.put("jda", jda);
        se.put("guild", guild);
        se.put("channel", channel);
        se.put("imagetimeout", new SendImageCommand(null, null));
        
        try
        {
            channel.sendMessage("Evaluated Successfully:\n```\n"+se.eval(message.getRawContent().split(" ",2)[1])+" ```").queue();
            message.addReaction(EmojiManager.getForAlias("white_check_mark").getUnicode()).queue();
        } 
        catch(Exception e)
        {
            channel.sendMessage("An exception was thrown:\n```\n"+e+" ```").queue();
            message.addReaction(EmojiManager.getForAlias("x").getUnicode()).queue();
        }
	}

}
