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
import wh1spr.bot.commands.points.shop.util.ShopGuild;
import wh1spr.bot.commands.points.shop.util.ShopItem;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.commands.points.util.PointsCommand;
import wh1spr.bot.reganplayz.EventBotPerm;

public class ShopAddCommand extends PointsCommand {

	public ShopAddCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!ShopAdd <name_with_underscores> <price|(>0)> <amount|(-1 or >0)>`\n`-1` is infinite.").build();
	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		if (!EventBotPerm.has(EventBotPerm.OWNER, invoker)) return;
		
		if (args.size() < 3) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		
		String name = args.get(0).replaceAll("_", " ");
		int amount;
		int price;
		ShopItem item = null;
		try {
			price = Integer.parseInt(args.get(1));
			amount = Integer.parseInt(args.get(2));
			
			item = new ShopItem(name, null, price, amount);
		} catch (Exception e) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		
		ShopGuild g = new ShopGuild(guild, this.getTypeManager());
		if(!g.addItem(item)) {
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Error!")
					.setDescription("Shops are limited to 10 items. Please remove an item first.").build()).queue();
			return;
		}
		
		channel.sendMessage(new EmbedBuilder().setColor(Color.green).setTitle(":white_check_mark: Success!")
				.setDescription(String.format("**Item added to shop!**%nName: **%s**"
						+ "%nPrice: **%d %s**%nAmount: **%s**", item.getName(), item.getPrice(),
						item.getPrice()==1?this.getTypeManager().getPointNameSing():this.getTypeManager().getPointNameMult(),
						item.getAmount()==-1?"Infinite":String.valueOf(item.getAmount()))).build()).queue();
		
		
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}
