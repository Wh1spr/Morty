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
	
	@Deprecated
	protected BasicMongoItem(String collection, String id) {
		this(collection, Long.parseLong(id));
	}
	
	/**
	 * A basic MongoDB Item. Contains ID of the item in a MongoDB and some helper functions.
	 * @param collection The MongoDB Collection this item is a part of. Database gets set by {@link Mongo}
	 * @param id The ID of this BasicMongoItem in the given collection in the database.
	 * @throws IllegalArgumentException if collection is null, id is smaller than 0
	 */
	protected BasicMongoItem(String collection, long id) {
		if (collection == null) throw new IllegalArgumentException("Collection cannot be null.");
		if (this.id < 0) throw new IllegalArgumentException("Item ID has to be greater or equal to 0.");
		log = LoggerCache.getLogger("MONGO");
		db.getCollection(collection); // will throw error if name is not valid.
		this.collection = collection;
		this.setId(id);
	}
	
	private String collection = null;
	
	/**
	 * @return Collection this item is a part of.
	 */
	public MongoCollection<Document> getCollection() {
		return db.getCollection(this.collection);
	}
	
	private long id = -1L;
	protected JDA jda = Main.getBot().getJDA();
	protected MongoDatabase db = Mongo.getDb();
	
	private void setId(long id) {
		if (this.id != -1L) throw new IllegalArgumentException("This item already has an ID.");
		this.id = id;
	}
	
	/**
	 * @see BasicMongoItem#getIdLong()
	 * @return The ID of the Item in the MongoDB.
	 */
	@Deprecated
	public String getId() {
		return String.valueOf(this.id);
	}
	
	/**
	 * @return The ID of the Item in the MongoDB.
	 */
	public long getIdLong() {
		return this.id;
	}
	
	/**
	 * @return The Document corresponding to the id of this item
	 * in the collection returned by {@link BasicMongoItem#getCollection()}
	 */
	public final Document getDoc() {
		Document doc = this.getCollection().find(eq("_id", getIdLong())).first();
		if (doc == null)  {
			doc = new Document("_id", this.getIdLong());
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
		Document item = this.getCollection().find(eq("_id", this.getIdLong())).first();
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
		this.getCollection().updateOne(eq("_id", this.getIdLong()), combine(asList(updates)));
	}
	
	/**
	 * Deletes this item in the database.
	 */
	protected void delete() {
		this.getCollection().deleteOne(eq("_id", this.getIdLong()));
	}
	
	protected static boolean exists(String collection, String id) {
		return exists(collection, Long.parseLong(id));
	}

	protected static boolean exists(String collection, long id) {
		return Mongo.getDb().getCollection(collection).find(eq("_id", id)).first()!=null;
	}
	
	/**
	 * Fills in the BasicMongoItem on creation of the document in DB
	 */
	protected abstract void create();
	
	/**
	 * Creates a Document in the DB, filled by {@link BasicMongoItem#create()}
	 */
	protected void createItem() {
		if (exists(this.collection, getIdLong()))
			throw new IllegalArgumentException("Illegal argument, ID " + id + " already exists in Collection " + collection);
		getCollection().insertOne(new Document("_id", id));
		create();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof BasicMongoItem) {
			BasicMongoItem b = (BasicMongoItem) o;
			if (Long.compare(b.getIdLong(), this.getIdLong()) == 0) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new Long(this.getIdLong()).hashCode();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.getIdLong() + "]";
	}
}
