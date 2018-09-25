package wh1spr.bot.commands.points.shop.util;

import static com.mongodb.client.model.Updates.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.PushOptions;

import net.dv8tion.jda.core.entities.Guild;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.mongodb.MongoGuild;

public class ShopGuild extends MongoGuild {

	public ShopGuild(Guild guild, PointTypeManager tm) {
		super(guild);
		if (tm == null) throw new IllegalArgumentException("PointTypeManager cannot be null.");
		this.tm = tm;
		
		if(!this.getDoc().containsKey(tm.getModuleName())) {
			this.setKey(tm.getModuleName(),
					new BasicDBObject(tm.getTypeName() + "shop", new ArrayList<BasicDBObject>()));
		} else if (!this.getDoc().get(tm.getModuleName(), Document.class).containsKey(tm.getTypeName() + "shop")) {
			this.bsonUpdates(set(tm.getModuleName() + "." + tm.getTypeName() + "shop", new ArrayList<BasicDBObject>()));
		}
	}
	
	private PointTypeManager tm = null;

	public boolean isEmpty() {
		return getShopArray().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public List<Document> getShopArray() {
		return (List<Document>)this.getDoc().get(tm.getModuleName(), Document.class).get(tm.getTypeName() + "shop");
	}
	
	private List<ShopItem> list = null;
	public List<ShopItem> getItems() {
		if (this.list!=null) return this.list;
		List<ShopItem> list = new ArrayList<ShopItem>();
		getShopArray().forEach(el -> {
			list.add(new ShopItem(el));
		});
		this.list = list;
		return list;
	}

	public ShopItem getItem(int index) {
		List<ShopItem> a = getItems();
		if (a.size() <= index) {
			return null;
		} else {
			return getItems().get(index);
		}
	}
	
	public boolean addItem(ShopItem item) {
		list = null;
		if (getShopArray().size() < 10) { //limit to 10 items
			this.bsonUpdates(push(tm.getModuleName() + "." + tm.getTypeName() + "shop", item.getDocument()));
			return true;
		} else {
			return false;
		}
	}
	
	public boolean bought(ShopItem item, int amount) { //amount in item is in shop
		if (getItems().indexOf(item)==-1) throw new IllegalArgumentException("This item does not exist in this store.");
		if (item.getAmount() < amount) {
			return false;
		} else {
			ShopItem clone = item.clone();
			if (item.getAmount()!=-1)
				clone.setAmount(item.getAmount() - amount);
			int index = getItems().indexOf(item);
			removeItem(index);
			list=null;
			this.bsonUpdates(pushEach(tm.getModuleName() + "." + tm.getTypeName() + "shop", Arrays.asList(clone.getDocument()), new PushOptions().position(index)));
			return true;
		}
	}
	
	public void removeItem(int index) {
		if (getShopArray().size() <= index) return;
		ShopItem toDel = getItems().get(index);
		list=null;
		this.bsonUpdates(pull(tm.getModuleName() + "." + tm.getTypeName() + "shop", toDel.getDocument()));
	}
	
	public void removeItem(ShopItem item) {
		list=null;
		this.bsonUpdates(pull(tm.getModuleName() + "." + tm.getTypeName() + "shop", item.getDocument()));
	}
}
