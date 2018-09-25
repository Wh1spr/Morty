package wh1spr.bot.commands.points.shop.util;

import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;

import net.dv8tion.jda.core.entities.User;
import wh1spr.bot.commands.points.util.PointTypeManager;
import wh1spr.bot.mongodb.MongoUser;

//inventory
public class ShopUser extends MongoUser {

	public ShopUser(User user, PointTypeManager tm) {
		this(user.getId(), tm);
	}
	public ShopUser(String userId, PointTypeManager tm) {
		super(userId);
		if (tm == null) throw new IllegalArgumentException("PointTypeManager cannot be null.");
		this.tm = tm;
		
		if(!this.getDoc().containsKey(tm.getModuleName())) {
			this.setKey(tm.getModuleName(),
					new BasicDBObject(tm.getTypeName() + "inv", new ArrayList<BasicDBObject>()));
		} else if (!this.getDoc().get(tm.getModuleName(), Document.class).containsKey(tm.getTypeName() + "inv")) {
			this.bsonUpdates(set(tm.getModuleName() + "." + tm.getTypeName() + "inv", new ArrayList<BasicDBObject>()));
		}
	}
	
	private PointTypeManager tm = null;
	
	public boolean isEmpty() {
		return getInventoryArray().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public List<Document> getInventoryArray() {
		return (List<Document>)this.getDoc().get(tm.getModuleName(), Document.class).get(tm.getTypeName() + "inv");
	}
	
	public List<ShopItem> getItems() {
		List<ShopItem> list = new ArrayList<ShopItem>();
		getInventoryArray().forEach(el -> {
			list.add(new ShopItem(el));
		});
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
		
		ShopItem equal = null;
		for (ShopItem s : getItems()) {
			if (s.getName().equals(item.getName()) && s.getPrice()==item.getPrice()) {
				equal = s;
				break;
			}
		}
		
		if (equal != null) {
			item.setAmount(equal.getAmount() + item.getAmount());
			removeItem(equal);
			this.bsonUpdates(push(tm.getModuleName() + "." + tm.getTypeName() + "inv", item.getDocument()));
			return true;
		} else if (getInventoryArray().size() <= 20) { 
			this.bsonUpdates(push(tm.getModuleName() + "." + tm.getTypeName() + "inv", item.getDocument()));
			return true;
		} else {
			return false;
		}
	}
	
	public void removeItem(int index) {
		if (getInventoryArray().size() <= index) return;
		ShopItem toDel = getItems().get(index);
		this.bsonUpdates(pull(tm.getModuleName() + "." + tm.getTypeName() + "inv", toDel.getDocument()));
	}
	
	public void removeItem(ShopItem item) {
		this.bsonUpdates(pull(tm.getModuleName() + "." + tm.getTypeName() + "inv", item.getDocument()));
	}
}
