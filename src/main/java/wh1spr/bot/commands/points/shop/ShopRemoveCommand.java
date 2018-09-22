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
import wh1spr.bot.commands.points.shop.util.ShopGuild;
import wh1spr.bot.commands.points.shop.util.ShopItem;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;
import wh1spr.bot.reganplayz.EventBotPerm;

public class ShopRemoveCommand extends PointsCommand {

	public ShopRemoveCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!ShopRemove <index|(>0)>`").build();
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!EventBotPerm.has(EventBotPerm.OWNER, invoker)) return;
		
		if (args.size() != 1 || !Tools.isPosInteger(args.get(0))) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		
		ShopGuild g = new ShopGuild(guild, this.getTypeManager());
		int index = Integer.parseInt(args.get(0));
		if (index == 0) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		ShopItem item = g.getItem(index - 1);
		if (item == null) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: This item does not exist").build()).queue();
			return;
		}
		g.removeItem(index - 1);
		
		channel.sendMessage(new EmbedBuilder().setColor(Color.green).setTitle(":white_check_mark: Success!")
				.setDescription(String.format("**Item removed from shop!**%nName: **%s**"
						+ "%nPrice: **%d %s**%nAmount: **%s**", item.getName(), item.getPrice(),
						item.getPrice()==1?this.getTypeManager().getPointNameSing():this.getTypeManager().getPointNameMult(),
						item.getAmount()==-1?"Infinite":String.valueOf(item.getAmount()))).build()).queue();
		
	}
	
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}
}
