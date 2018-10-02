package wh1spr.bot.mongodb;

public abstract class BasicUpdateMongoItem extends BasicMongoItem {

	protected BasicUpdateMongoItem(String collection, String id) {
		super(collection, id);
		this.update();
	}

	protected abstract boolean update();

}
