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
import wh1spr.bot.commands.mod.util.Kick;
import wh1spr.bot.commands.mod.util.KickUser;
import wh1spr.bot.mongodb.MongoUser;

public class GetKickCommand extends Command {

	public GetKickCommand(String name, String... aliases) {
		super(name, aliases);
		this.setMaelstromOnly(false);
	}

	private static MessageEmbed failnonexistent = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: This kick does not exist.").build();
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!GetKick <hex value | @user | userId>`").build();
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check, anyone can see any warning/ban/kick
		
		if (args.size() != 1) {
			channel.sendMessage(failsyntax).queue();
			return;
		} else if (message.getMentionedUsers().isEmpty() && !MongoUser.exists(args.get(0)) && (Tools.isPosInteger(args.get(0))?jda.getUserById(args.get(0))==null:true)) {
			// hex val
			if (!Kick.exists(args.get(0))) {
				channel.sendMessage(failnonexistent).queue();
				return;
			} else {
				Kick k = new Kick(args.get(0));
				if(!k.isSet()) {
					channel.sendMessage(failnonexistent).queue();
					return;
				}
				
				channel.sendMessage(new EmbedBuilder().setColor(Color.orange).setTitle(":warning: **Ban " + k.getHexString().toUpperCase() + "**")
						.setDescription(String.format("User: **%s**%nBy: **%s**%nServer: **%s**%nReason: *%s*%nDate: **%s**", k.getUsername(), k.getIssuername(),
								k.getGuildName(), k.getReason().trim().equals("")?"No reason":k.getReason().trim(), k.getDate().replaceAll("_", " "))).build()).queue();
			}
		} else {
			KickUser ku = null;
			if (!message.getMentionedUsers().isEmpty()) {
				ku = new KickUser(message.getMentionedUsers().get(0));
			} else { 
				ku = new KickUser(args.get(0));
			}
			List<Kick> kicks = ku.getKicks();
			
			if (kicks.isEmpty()) {
				channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle(":white_check_mark: **" + ku.getUserMention() + "** has no kicks!").build()).queue();
				return;
			} else {
				//divide in this guild and others (ALSO CHECK NULL)
				if (guild==null) { //private message
					String w = getKicksFromList(kicks);
					
					channel.sendMessage(new EmbedBuilder().setColor(Color.ORANGE)
							.setTitle(String.format("**%s** has **%d** ban%s.", ku.getUserMention(), kicks.size(), kicks.size()==1?"":"s"))
							.setDescription("**Hex Values:** " + w).build()).queue();
				} else {
					List<Kick> guildkicks = new ArrayList<Kick>();
					kicks.forEach(el->{if (el.getGuildId().equals(guild.getId())) {guildkicks.add(el);}});
					kicks.removeAll(guildkicks);
					
					EmbedBuilder eb = new EmbedBuilder().setColor(Color.ORANGE)
							.setTitle(String.format("**%s** has **%d** ban%s.", ku.getUserMention(), kicks.size(), kicks.size()==1?"":"s"));
					if (!guildkicks.isEmpty()) eb.addField("**This server**", "**Hex Values:** " + getKicksFromList(guildkicks), true);
					if (!kicks.isEmpty()) eb.addField("**Other servers:** ", "**Hex Values:** " + getKicksFromList(kicks), true);
					
					channel.sendMessage(eb.build()).queue();
				}
			}
		}
		
	}
	
	private static String getKicksFromList(List<Kick> kicks) {
		String w = "";
		for (Kick wa : kicks) {
			w += wa.getHexString().toUpperCase() + ", ";
		}
		return w.substring(0, w.length()-2);
	}
}
