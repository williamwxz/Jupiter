package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection{

	private Connection conn;
	
	private PreparedStatement saveItemStmt = null;
	
	@SuppressWarnings("unused")
	private PreparedStatement getSaveItemStmt() {
		try {
			if (conn == null) {
				System.err.println("No Databse connection");
				return null;
			}
			saveItemStmt = conn.prepareStatement("INSERT IGNORE INTO items VALUE(?, ?, ?, ?, ?, ?, ?)");
		}catch (Exception e) {
			e.printStackTrace();
		}
		return saveItemStmt;
	}
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLUtil.URL);
		}catch (Exception e) {
			close();
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (conn!=null) {
			try {
				conn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		if (conn==null) {
			System.err.println("DB has not yet started");
			return;
		}
		
		try {
			String query = "INSERT IGNORE INTO history(user_id, item_id) VALUES(?, ?)";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, userId);
			for (String itemId: itemIds) {
				stmt.setString(2, itemId);
				stmt.execute();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		if (conn==null) {
			System.err.println("DB has not yet started");
			return;
		}
		try {
			String query = "DELETE FROM history WHERE user_id=? AND item_id=?";
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, userId);
			for (String itemId: itemIds) {
				stmt.setString(2, itemId);
				stmt.execute();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<String> favoriteItemIds = new HashSet<>();
		
		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItemIds.add(itemId);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return favoriteItemIds;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if(conn==null) {
			System.err.println("DB connection has not been init");
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> favoriteItemsIds = getFavoriteItemIds(userId);
		
		try {
			String query = "SELECT * FROM items WHERE item_id=?";
			PreparedStatement st = conn.prepareStatement(query);
			for (String itemId: favoriteItemsIds) {
				st.setString(1, itemId);
				ResultSet rs = st.executeQuery();
				
				Item.ItemBuilder builder = new Item.ItemBuilder();
				while (rs.next()) {
					builder.setItemID(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageURL(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(userId));
					builder.setRating(rs.getDouble("rating"));
					builder.setDistance(rs.getDouble("distance"));
				}
				favoriteItems.add(builder.build());
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		if (conn==null) {
			System.err.println("DB connection has not init");
			return new HashSet<>();
		}
		Set<String> categories = new HashSet<>();
		try {
			String query = "SELECT category FROM categories WHERE item_id=?";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, itemId);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				categories.add(rs.getString("category"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// TODO Auto-generated method stub
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.EventSearch(lat, lon, term);
		for (Item item: items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		// TODO Auto-generated method stub
		if (conn==null) {
			System.err.println("[saveItem]No Database exits");
			return;
		}
		try {
//			String query = String.format("INSERT IGNORE INTO items("
//					+ "%s, %s, %s, %s, %s, %s, %s)",
//					item.getItemID(),
//					item.getName(),
//					item.getRating(),
//					item.getAddress(),
//					item.getImageURL(),
//					item.getUrl(),
//					item.getDistance());
//			Statement st = conn.createStatement();
//			st.execute(query);
//			Above will cause SQL Injection, type of database hacking
			
			String query = "INSERT IGNORE INTO items VALUE(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, item.getItemID());
			st.setString(2, item.getName());
			st.setDouble(3, item.getRating());
			st.setString(4, item.getAddress());
			st.setString(5, item.getImageURL());
			st.setString(6, item.getUrl());
			st.setDouble(7, item.getDistance());
			st.execute();
			
			query = "INSERT IGNORE INTO categories VALUE(?, ?)";
			for (String category:item.getCategories()) {
				st = conn.prepareStatement(query);
				st.setString(1, item.getItemID());
				st.setString(2, category);
				st.execute();
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
