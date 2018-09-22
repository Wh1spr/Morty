package wh1spr.bot.mongodb;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;

import net.dv8tion.jda.core.JDA;
import wh1spr.bot.Main;
import wh1spr.logger.Logger;
import wh1spr.logger.LoggerCache;
/**
 * Constructor of subclasses MUST make sure that the item exists in Database before finishing.
 */
public abstract class BasicMongoItem {

	protected Logger log;
	
	/**
	 * A basic MongoDB Item. Contains ID of the item in a MongoDB and some helper functions.
	 * @param collection The MongoDB Collection this item is a part of. Database gets set by {@link MongoDB}
	 * @throws IllegalArgumentException if collection is null.
	 */
	protected BasicMongoItem(String collection) {
		if (collection == null) throw new IllegalArgumentException("Collection cannot be null.");
		log = LoggerCache.getLogger("MONGO");
		db.getCollection(collection); // will throw error if name is not valid.
		this.collection = collection;
		this.update();
	}
	
	private String collection = null;
	
	/**
	 * @return Collection this item is a part of.
	 */
	public MongoCollection<Document> getCollection() {
		return db.getCollection(this.collection);
	}
	
	private String id = null;
	protected JDA jda = Main.getBot().getJDA();
	protected MongoDatabase db = MongoDB.getDb();
	
	/**
	 * Sets the ID of the item in the database.
	 * @param id the Id of the item.
	 * @throws IllegalArgumentException if the item already had an ID set.
	 */
	protected void setId(String id) {
		if (this.id != null) throw new IllegalArgumentException("This item already has an ID.");
		this.id = id;
	}
	/**
	 * @return The ID of the Item in the MongoDB.
	 */
	public String getId() {
		return this.id;
	}
	/**
	 * @return The Document corresponding to the id of this item
	 * in the collection returned by {@link BasicMongoItem#getCollection()}
	 */
	public final Document getDoc() {
		Document doc = this.getCollection().find(eq("_id", getId())).first();
		if (doc == null)  {
			doc = new Document("_id", this.getId());
			this.getCollection().insertOne(doc);
		}
		return doc;
	}
	
	/**
	 * Maps the given key to the given value in the MongoDB.
	 * @param key The key to map.
	 * @param value The value to map to the key.
	 */
	protected void setKey(String key, Object value) {
		this.bsonUpdates(set(key, value));
	}
	
	/**
	 * @return Wether or not this key has a mapping.
	 */
	protected boolean hasKey(String key) {
		Document item = this.getCollection().find(eq("_id", this.getId())).first();
		if (item == null) return false; // shouldnt happen but hell who knows
		else {
			return item.containsKey(key);
		}
	}
	/**
	 * Deletes given key in the MongoDB.
	 * @param key Key to delete.
	 * @return false if key didn't exist.
	 * @return true if the key was deleted.
	 */
	protected boolean deleteKey(String key) {
		if (!hasKey(key)) return false;
		else {
			this.bsonUpdates(unset(key));
			return true;
		}
	}
	
	protected void bsonUpdates(Bson... updates) {
		this.getCollection().updateOne(eq("_id", this.getId()), combine(asList(updates)));
	}
	
	/**
	 * Deletes this item in the database.
	 */
	protected void delete() {
		this.getCollection().deleteOne(eq("_id", this.getId()));
	}
	
	protected abstract boolean update();

	protected static boolean exists(String collection, String hex) {
		return MongoDB.getDb().getCollection(collection).find(eq("_id", hex)).first()!=null;
	}
}
