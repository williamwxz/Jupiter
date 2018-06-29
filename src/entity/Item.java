package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	private static final String ITEM_ID = "item_id"; 
	private static final String NAME = "name";
	private static final String RATING = "rating";
	private static final String ADDRESS = "address";
	private static final String CATEGORIES = "categories";
	private static final String IMAGE_URL = "image_url";
	private static final String URL = "url";
	private static final String DISTANCE = "distance";
	
	private String itemID;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageURL;
	private String url;
	private double distance;
	
	public String getItemID() {
		return itemID;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageURL() {
		return imageURL;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(ITEM_ID, itemID);
			obj.put(NAME, name);
			obj.put(RATING, rating);
			obj.put(ADDRESS, address);
			obj.put(CATEGORIES, new JSONArray(categories));
			obj.put(IMAGE_URL, imageURL);
			obj.put(URL, url);
			obj.put(DISTANCE, distance);
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static class ItemBuilder{
		private String itemID;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageURL;
		private String url;
		private double distance;
		
		public void setItemID(String itemID) {
			this.itemID = itemID;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setRating(double rating) {
			this.rating = rating;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}
		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		public Item build() {
			return new Item(this);
		}
	}
	
	private Item(ItemBuilder builder) {
		this.itemID = builder.itemID;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageURL = builder.imageURL;
		this.url = builder.url;
		this.distance = builder.distance;
	}
}
