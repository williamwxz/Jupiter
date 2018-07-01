package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTableCreate {

	public static void main(String[] args) {
		try {
			System.out.println("Connecting to "+MySQLUtil.DB_NAME);
			//register a driver
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
//			Class.forName("com.mysql.jdbc.Driver"); //Works in Java 7 or above
			//get the connection from registered driver
			Connection conn = DriverManager.getConnection(MySQLUtil.URL);
			if (conn == null) {
				return;
			}
			System.out.println("Import done sucessfully");
			
			Statement st = conn.createStatement();
			
			// Test deletion
			String query = "DROP TABLE IF EXISTS categories";
			st.executeUpdate(query);
			
			query = "DROP TABLE IF EXISTS history";
			st.executeUpdate(query);
			
			query = "DROP TABLE IF EXISTS items";
			st.executeUpdate(query);
			
			query = "DROP TABLE IF EXISTS users";
			st.executeUpdate(query);
			System.out.println("Successfully DROP table");
			// Test Creation
			query = "CREATE TABLE items ("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255),"
					+ "rating FLOAT,"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "url VARCHAR(255),"
					+ "distance FLOAT,"
					+ "PRIMARY KEY (item_id)"
					+ ")";
			st.executeUpdate(query);
			
			query = "CREATE TABLE categories("
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (item_id, category),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id)"
					+ ")";
			st.executeUpdate(query);
			
			query = "CREATE TABLE users("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id)"
					+ ")";
			st.executeUpdate(query);
			
			query = "CREATE TABLE history ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "item_id VARCHAR(255) NOT NULL,"
					+ "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "PRIMARY KEY (user_id, item_id),"
					+ "FOREIGN KEY (item_id) REFERENCES items(item_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id)"
					+ ")";
			st.executeUpdate(query);
			System.out.println("Done creating tables");
			
			query = "INSERT INTO users VALUES("
					+ "'1111', '2222', 'John', 'Smith'"
					+")";
			st.executeUpdate(query);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
