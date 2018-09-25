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
import wh1spr.bot.commands.points.shop.util.*;
import wh1spr.bot.commands.points.util.*;
import wh1spr.bot.mongodb.MongoUser;

public class BuyCommand extends PointsCommand {

	public BuyCommand(String name, PointTypeManager tm, String... aliases) {
		super(name, tm, aliases);
	}
	
	private static MessageEmbed failsyntax = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: Incorrect Syntax.").setDescription("`E!Buy <id in shop> <amount>0>`").build();
	private static MessageEmbed failnosuchitem = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: There is no such item in the shop!").build();
	private static MessageEmbed failnei = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: The shop doesn't have that many!").build();
	private static MessageEmbed failnep = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You don't have enough money!").build();
	private static MessageEmbed failnespace = new EmbedBuilder().setColor(Color.RED).setTitle(":no_entry_sign: You don't have enough space in your inventory!").build();

	
	@Override
	public void onCall(JDA jda, Guild guild, MessageChannel channel, User invoker, Message message, List<String> args) {
		//no perm check
		if (args.size()!=2) {
			channel.sendMessage(failsyntax).queue();
			return;
		} else if(!Tools.isPosInteger(args.get(0)) || !Tools.isPosInteger(args.get(1))) {
			channel.sendMessage(failsyntax).queue();
			return;
		}
		
		ShopUser su = new ShopUser(invoker, this.getTypeManager());
		PointsUser pu = this.getTypeManager().getPointsUser(new MongoUser(invoker));
		ShopGuild g = new ShopGuild(guild, this.getTypeManager());
		
		int id = Integer.parseInt(args.get(0)) - 1;
		int amount = Integer.parseInt(args.get(1));
		ShopItem toBuy = g.getItem(id);
		
		if (amount < 1) {
			channel.sendMessage(failsyntax).queue();
			return;
		} else if (toBuy == null) {
			channel.sendMessage(failnosuchitem).queue();
			return;
		} else if (toBuy.getAmount() < amount && toBuy.getAmount()!=-1) {
			channel.sendMessage(failnei).queue();
			return;
		} else if (toBuy.getPrice()*amount > pu.getPoints()) {
			channel.sendMessage(failnep).queue();
			return;
		} else if (su.getItems().size() == 20) {
			channel.sendMessage(failnespace).queue();
			return;
		}
		
		//we have enough points AND the shop has enough items AND we have space in our inventory
		pu.setPoints(pu.getPoints() - toBuy.getPrice()*amount);
		g.bought(toBuy, amount);
		su.addItem(toBuy.setAmount(amount));
		
		channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle(":white_check_mark: Successfully bought item!")
				.setDescription(String.format("Name: **%s**%nBought: **%d**%nPrice: **%s**",
						toBuy.getName(), amount, toBuy.getPrice()==0?"FREE":(toBuy.getPrice()*toBuy.getAmount()==1?
								this.getTypeManager().getPointNameSing():this.getTypeManager().getPointNameMult()))).build()).queue();
		
	}
	
	@Override
	public void onCallPrivate(JDA jda, MessageChannel channel, User invoker, Message message, List<String> args) {
		cantUse(channel);
	}

}
