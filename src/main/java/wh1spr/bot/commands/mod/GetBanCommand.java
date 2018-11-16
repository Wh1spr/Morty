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
import wh1spr.bot.commands.mod.util.Ban;
import wh1spr.bot.commands.mod.util.BanUser;
import wh1spr.bot.mongodb.MongoUser;

public class GetBanCommand extends Command {

	public GetBanCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	private static MessageEmbed failnonexistent = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: This ban does not exist.").build();
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!GetBan <hex value | @user | userId>`").build();
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check, anyone can see any warning/ban/kick
		
		if (args.size() != 1) {
			channel.sendMessage(failsyntax).queue();
			return;
		} else if (message.getMentionedUsers().isEmpty() && !MongoUser.exists(args.get(0)) && (Tools.isPosInteger(args.get(0))?jda.getUserById(args.get(0))==null:true)) {
			// hex val
			if (!Ban.exists(args.get(0))) {
				channel.sendMessage(failnonexistent).queue();
				return;
			} else {
				Ban b = new Ban(args.get(0));
				if(!b.isSet()) {
					channel.sendMessage(failnonexistent).queue();
					return;
				}
				
				channel.sendMessage(new EmbedBuilder().setColor(Color.orange).setTitle(":warning: **Ban " + b.getHexString().toUpperCase() + "**")
						.setDescription(String.format("User: **%s**%nBy: **%s**%nServer: **%s**%nReason: *%s*%nDate: **%s**", b.getUsername(), b.getIssuername(),
								b.getGuildName(), b.getReason().trim().equals("")?"No reason":b.getReason().trim(), b.getDate().replaceAll("_", " "))).build()).queue();
			}
		} else {
			BanUser bu = null;
			if (!message.getMentionedUsers().isEmpty()) {
				bu = new BanUser(message.getMentionedUsers().get(0));
			} else { 
				bu = new BanUser(args.get(0));
			}
			List<Ban> bans = bu.getBans();
			
			if (bans.isEmpty()) {
				channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle(":white_check_mark: **" + bu.getUserMention() + "** has no bans!").build()).queue();
				return;
			} else {
				//divide in this guild and others (ALSO CHECK NULL)
				if (guild==null) { //private message
					String w = getBansFromList(bans);
					
					channel.sendMessage(new EmbedBuilder().setColor(Color.ORANGE)
							.setTitle(String.format("**%s** has **%d** ban%s.", bu.getUserMention(), bans.size(), bans.size()==1?"":"s"))
							.setDescription("**Hex Values:** " + w).build()).queue();
				} else {
					List<Ban> guildwarns = new ArrayList<Ban>();
					bans.forEach(el->{if (el.getGuildId().equals(guild.getId())) {guildwarns.add(el);}});
					bans.removeAll(guildwarns);
					
					EmbedBuilder eb = new EmbedBuilder().setColor(Color.ORANGE)
							.setTitle(String.format("**%s** has **%d** ban%s.", bu.getUserMention(), bans.size(), bans.size()==1?"":"s"));
					if (!guildwarns.isEmpty()) eb.addField("**This server**", "**Hex Values:** " + getBansFromList(guildwarns), true);
					if (!bans.isEmpty()) eb.addField("**Other servers:** ", "**Hex Values:** " + getBansFromList(bans), true);
					
					channel.sendMessage(eb.build()).queue();
				}
			}
		}
		
	}
	
	private static String getBansFromList(List<Ban> bans) {
		String w = "";
		for (Ban wa : bans) {
			w += wa.getHexString().toUpperCase() + ", ";
		}
		return w.substring(0, w.length()-2);
	}
}
