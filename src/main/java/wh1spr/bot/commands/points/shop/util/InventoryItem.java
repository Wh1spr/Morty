package wh1spr.bot.commands.points.shop.util;

import org.bson.Document;

public class InventoryItem extends ShopItem {

	public InventoryItem(ShopItem item, int owned) {
		super(item.getName(), item.getDesc(), item.getPrice(), item.getAmount());
		if (owned <= 0) throw new IllegalArgumentException("Owned must be at least 1.");
		
		this.owned = owned;
	}
	
	public InventoryItem(Document doc) {
		super(doc);
		this.owned = doc.getInteger("owned", 1);
	}
	
	private int owned = 0;
	
	public int getOwned() {
		return this.owned;
	}
	
	@Override
	public Document getDocument() {
		return super.getDocument().append("owned", getOwned());
	}
	
	

}
