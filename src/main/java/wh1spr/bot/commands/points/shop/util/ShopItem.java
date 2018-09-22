package wh1spr.bot.commands.points.shop.util;

import org.bson.Document;

public class ShopItem { // not basicmongoitem

	ShopItem(Document d) {
		this.name = d.getString("name");
		this.desc = d.getString("desc");
		this.price = d.getInteger("price", 0);
		this.amount = d.getInteger("amount", -1);
	}
	
	public ShopItem(String name, String desc, int price, int amount) {
		if (name == null) throw new IllegalArgumentException("Name cannot be null.");
		if (desc == null) desc = "No Description";
		if (price < 0) throw new IllegalArgumentException("Price cannot be lower than 0.");
		if (amount < 1 && amount != -1) throw new IllegalArgumentException("There needs to be at least one of this item.");
		
		this.name = name;
		this.desc = desc;
		this.price = price;
		this.amount = amount;
	}
	
	private String name = null;
	private String desc = null;
	private int price = 0;
	private int amount = 0;
	
	public String getName() {return this.name;}
	public String getDesc() {return this.desc;}
	public int getPrice() {return this.price;}
	public int getAmount() {return this.amount;}
	
	public Document getDocument() {
		return new Document("name", getName()).append("desc", getDesc())
				.append("price", getPrice()).append("amount", getAmount());
	}
}
