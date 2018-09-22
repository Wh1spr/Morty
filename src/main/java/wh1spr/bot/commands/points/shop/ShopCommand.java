package wh1spr.bot.commands.points.shop;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.commands.points.shop.util.*;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;

public class ShopCommand extends PointsCommand {

	public ShopCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failempty = new EmbedBuilder().setColor(Color.red).setTitle(":no_entry_sign: There is no shop!").build();

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		ShopGuild g = new ShopGuild(guild, this.getTypeManager());
		
		if (g.isEmpty()) {
			channel.sendMessage(failempty).queue();
			return;
		}
		
		//I'm gonna limit to 10 items for now
		List<ShopItem> items = g.getItems();
		EmbedBuilder e = new EmbedBuilder().setColor(Color.CYAN).setTitle(String.format("**%s's Shop**", guild.getName()));
		String fieldmsg = "";
		int i = 1;
		for(ShopItem item : items) {
			fieldmsg += (i==1?"":"\n");
			fieldmsg += String.format("%d. **%s** - %17s", i, item.getName(), item.getPrice()==0?"Free!":String.format("%d %s", item.getPrice(), item.getPrice()==1?this.getTypeManager().getPointNameSing():this.getTypeManager().getPointNameMult()));
			if (item.getAmount() != -1) {
				fieldmsg += item.getAmount()==0?"\n^  *SOLD OUT*":String.format("%n^  *%d remaining!*", item.getAmount());
			}
			i++;
		}
		e.addField("Items", fieldmsg, false);
		channel.sendMessage(e.build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}
