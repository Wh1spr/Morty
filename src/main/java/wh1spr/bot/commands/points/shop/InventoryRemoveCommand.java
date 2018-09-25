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

public class InventoryRemoveCommand extends PointsCommand {

	public InventoryRemoveCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!InvRemove <index|(>0)>`").build();
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (args.size() != 1 || !Tools.isPosInteger(args.get(0))) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		
		ShopUser s = new ShopUser(invoker, this.getTypeManager());
		int index = Integer.parseInt(args.get(0));
		if (index == 0) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		ShopItem item = s.getItem(index - 1);
		if (item == null) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: This item does not exist").build()).queue();
			return;
		}
		s.removeItem(index - 1);
		
		channel.sendMessage(new EmbedBuilder().setColor(Color.green).setTitle(":white_check_mark: Success!")
				.setDescription(String.format("**Item removed from inventory!**%nName: **%s**"
						+ "%nAmount: **%s**", item.getName(), String.valueOf(item.getAmount()))).build()).queue();
		
	}
	
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}
}