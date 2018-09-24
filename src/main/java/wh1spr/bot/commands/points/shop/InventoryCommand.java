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
import wh1spr.bot.Tools;
import wh1spr.bot.commands.points.shop.util.ShopItem;
import wh1spr.bot.commands.points.shop.util.ShopUser;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;

public class InventoryCommand extends PointsCommand {

	public InventoryCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!Inventory [@user|userId]`").build();
	private static MessageEmbed failempty = new EmbedBuilder().setColor(Color.red).setTitle(":no_entry_sign: There are no items in this inventory!").build();

	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		ShopUser u = null;
		if (message.getMentionedUsers().size()==1 && args.size() == 1) {
			u = new ShopUser(message.getMentionedUsers().get(0), this.getTypeManager());
		} else if (args.size() == 1) {
			if (Tools.isPosInteger(args.get(0))) {
				if (jda.getUserById(args.get(0)) != null) {
					u = new ShopUser(jda.getUserById(args.get(0)), this.getTypeManager());
				} else {
					if (ShopUser.exists(args.get(0))) {
						u = new ShopUser(args.get(0), this.getTypeManager());
					} else {
						channel.sendMessage(failsyntax).queue();
						return;
					}
				}
			} else {
				channel.sendMessage(failsyntax).queue();
				return;
			}
		} else if (!args.isEmpty()) {
			channel.sendMessage(failsyntax).queue();
			return;
		} else {
			u = new ShopUser(invoker, this.getTypeManager());
		}
		
		if (u.isEmpty()) {
			channel.sendMessage(failempty).queue();
			return;
		}
		
		//I'm gonna limit to 20 items for now
		List<ShopItem> items = u.getItems();
		EmbedBuilder e = new EmbedBuilder().setColor(Color.CYAN).setTitle(String.format("**%s's Inventory**", u.getUserMention()));
		String fieldmsg = "";
		int i = 1;
		for(ShopItem item : items) {
			fieldmsg += (i==1?"":"\n");
			fieldmsg += String.format("%d. **%s** - %17s ", i, item.getName(), item.getAmount() + " owned.");
			i++;
		}
		e.addField("Items", fieldmsg, true);
		channel.sendMessage(e.build()).queue();
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}
