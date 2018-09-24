package wh1spr.bot.commands.mod;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.Tools;
import wh1spr.bot.command.Command;
import wh1spr.bot.commands.mod.util.WarnUser;
import wh1spr.bot.commands.mod.util.Warning;
import wh1spr.bot.mongodb.MongoUser;

public class GetWarnCommand extends Command {

	public GetWarnCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	private static MessageEmbed failnonexistent = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: This warning does not exist.").build();
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!GetWarn <hex value | @user | userId>").build();
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check, anyone can see any warning/ban/kick
		
		if (args.size() != 1) {
			channel.sendMessage(failsyntax).queue();
			return;
		} else if (message.getMentionedUsers().isEmpty() && !MongoUser.exists(args.get(0)) && Tools.isPosInteger(args.get(0))?jda.getUserById(args.get(0))==null:true) {
			// hex val
			if (!Warning.exists(args.get(0))) {
				channel.sendMessage(failnonexistent).queue();
				return;
			} else {
				Warning w = new Warning(args.get(0));
				if(!w.isSet()) {
					channel.sendMessage(failnonexistent).queue();
					return;
				}
				
				channel.sendMessage(new EmbedBuilder().setColor(Color.orange).setTitle(":warning: **Warning " + w.getHexString().toUpperCase() + "**")
						.setDescription(String.format("User: **%s**%nBy: **%s**%nServer: **%s**%nReason: *%s*%nDate: **%s**", w.getUsername(), w.getIssuername(),
								w.getGuildName(), w.getReason().trim().equals("")?"No reason":w.getReason().trim(), w.getDate().replaceAll("_", " "))).build()).queue();
			}
		} else {
			WarnUser wu = null;
			if (!message.getMentionedUsers().isEmpty()) {
				wu = new WarnUser(message.getMentionedUsers().get(0));
			} else { 
				wu = new WarnUser(args.get(0));
			}
			List<Warning> warns = wu.getWarnings();
			
			if (warns.isEmpty()) {
				channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle(":white_check_mark: **" + wu.getUserMention() + "** has no warnings!").build()).queue();
				return;
			} else {
				//divide in this guild and others (ALSO CHECK NULL)
				if (guild==null) { //private message
					String w = getWarningsFromList(warns);
					
					channel.sendMessage(new EmbedBuilder().setColor(Color.ORANGE)
							.setTitle(String.format("**%s** has **%d** warning%s.", wu.getUserMention(), warns.size(), warns.size()==1?"":"s"))
							.setDescription("**Hex Values:** " + w).build()).queue();
				} else {
					List<Warning> guildwarns = new ArrayList<Warning>();
					warns.forEach(el->{if (el.getGuildId().equals(guild.getId())) {guildwarns.add(el);}});
					warns.removeAll(guildwarns);
					
					EmbedBuilder eb = new EmbedBuilder().setColor(Color.ORANGE)
							.setTitle(String.format("**%s** has **%d** warning%s.", wu.getUserMention(), warns.size(), warns.size()==1?"":"s"));
					if (!guildwarns.isEmpty()) eb.addField("**This server**", "**Hex Values:** " + getWarningsFromList(guildwarns), true);
					if (!warns.isEmpty()) eb.addField("**Other servers:** ", "**Hex Values:** " + getWarningsFromList(warns), true);
					
					channel.sendMessage(eb.build()).queue();
				}
			}
		}
		
	}
	
	private static String getWarningsFromList(List<Warning> warns) {
		String w = "";
		for (Warning wa : warns) {
			w += wa.getHexString().toUpperCase() + ", ";
		}
		return w.substring(0, w.length()-2);
	}
}
