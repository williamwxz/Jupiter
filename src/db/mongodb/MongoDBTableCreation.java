package db.mongodb;

import java.text.ParseException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBTableCreation {
	private static final String USERS="users";
	private static final String ITEMS = "items";
	private static final String USER_ID="user_id";
	private static final String ITEM_ID="item_id";
	public static void main(String[] args) throws ParseException{
		// Connect to MongoDB
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
		
		// remove old connections
		db.getCollection(USERS).drop();
		db.getCollection(ITEMS).drop();
		
		// create new collections
		IndexOptions options = new IndexOptions().unique(true);
		db.getCollection(USERS).createIndex(new Document(USER_ID, 1), options);
		db.getCollection(ITEMS).createIndex(new Document(ITEM_ID, 1), options);
		
		// insert fake user data and create index
		Document test = new Document();
		test.append(USER_ID, "1111");
		test.append("password", "3229c1097c00d497a0fd282d586be050");
		test.append("first_name", "John");
		test.append("last_name", "Smith");
		db.getCollection(USERS).insertOne(test);
		
		mongoClient.close();
		System.out.println("Import is done successfully");
	}
}
