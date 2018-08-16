package db.mongodb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MongoDBConnection implements DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	private static final String USER_ID = "user_id";
	private static final String ITEM_ID = "item_id";
	private static final String NAME = "name";
	private static final String URL = "url";
	private static final String RATING = "rating";
	private static final String DISTANCE = "distance";
	private static final String CATEGORIES = "categories";
	private static final String FAVORITE = "favorite";
	private static final String ADDRESS = "address";
	
	private static final String USER_DB = "users";
	private static final String ITEM_DB = "items";
	
	public MongoDBConnection() {
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}
	
	@Override
	public void close() {
		if (mongoClient!=null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		Document userDoc = new Document();
		userDoc.append(USER_ID, userId);
		Document pushDoc = new Document();
		pushDoc.append("$push", new Document(
				FAVORITE, new Document(
							"$each", itemIds
							)
		));
		db.getCollection(USER_DB).updateOne(userDoc, pushDoc);
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		Document input = new Document();
		input.append(USER_ID, new Document(
				"$pullAll", new Document(
						FAVORITE, itemIds
						)
				));
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		Set<String> favoriteItems = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection(USER_DB).find(eq(USER_ID, userId));
		
		if (iterable.first()!=null && iterable.first().containsKey(FAVORITE)) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get(FAVORITE);
			favoriteItems.addAll(list);
		}
		return favoriteItems;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		Set<Item> items = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		for (String itemId: itemIds) {
			FindIterable<Document> iterable = db.getCollection(ITEM_DB).find(eq(ITEM_ID, itemId));
			if (iterable.first()!=null) {
				Document doc = iterable.first();
				
				ItemBuilder builder = new ItemBuilder();
				builder.setItemID(doc.getString(ITEM_ID));
				builder.setName(doc.getString(NAME));
				builder.setAddress(doc.getString(ADDRESS));
				builder.setUrl(doc.getString(URL));
				builder.setImageURL(doc.getString("image_url"));
				builder.setRating(doc.getDouble(RATING));
				builder.setDistance(doc.getDouble(DISTANCE));
				builder.setCategories(getCategories(itemId));
				
				items.add(builder.build());
			}
		}
		return items;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection(ITEM_DB).find(eq(ITEM_ID, itemId));
		
		if (iterable.first()!=null && iterable.first().containsKey(CATEGORIES)) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get(CATEGORIES);
			categories.addAll(list);
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI api = new TicketMasterAPI();
		List<Item> items = api.EventSearch(lat, lon, term);
		for (Item item:items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		FindIterable<Document> iterable = db.getCollection(ITEM_DB).find(eq(ITEM_ID, item.getItemID()));
		if (iterable.first()==null) {
			Document input = new Document();
			input.append(ITEM_ID, item.getItemID());
			input.append(DISTANCE, item.getDistance());
			input.append(NAME, item.getName());
			input.append(URL, item.getUrl());
			input.append("image_url", item.getImageURL());
			input.append(RATING, item.getRating());
			input.append(CATEGORIES, item.getCategories());
			db.getCollection("item").insertOne(input);
		}
	}

	@Override
	public String getFullname(String userId) {
		FindIterable<Document> iterable = db.getCollection(USER_DB).find(eq(USER_ID, userId));
		if (iterable.first()!=null) {
			Document doc = iterable.first();
			return doc.getString("first_name") + " " + doc.getString("last_name");
		}
		return "";
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		FindIterable<Document> iterable = db.getCollection(USER_DB).find(eq(USER_ID, userId));
		if (iterable.first()!=null) {
			System.out.println("Found user: "+userId);
			return iterable.first().getString("password").equals(password);
		}
		return false;
	}
}
