package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;


public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendations = new ArrayList<>();

		DBConnection conn = DBConnectionFactory.getConnection();
		
		try {
			// Get favorite items by user id
			Set<String> favoriteItems = conn.getFavoriteItemIds(userId);
			// Get categories of items, sort by count
			Map<String, Integer> allCategories = new HashMap<>();
			for (String itemID: favoriteItems) {
				Set<String> categories = conn.getCategories(itemID);
				for (String category: categories) {
					allCategories.put(category, allCategories.getOrDefault(category, 0)+1);
				}
			}
			List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
			Collections.sort(categoryList, new Comparator<Entry<String, Integer>>(){
				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return Integer.compare(o2.getValue(), o1.getValue());
				}
			});
			
			// Filter out favorite events, sort by distance
			Set<Item> visitedItems = new HashSet<>();
			for (Entry<String, Integer> category: categoryList) {
				List<Item> items = conn.searchItems(lat, lon, category.getKey());
				List<Item> filteredItems = new ArrayList<>();
				
				for (Item item: items) {
					if (!favoriteItems.contains(item.getItemID()) && !visitedItems.contains(item)) {
						filteredItems.add(item);
					}
				}
				
				Collections.sort(filteredItems, new Comparator<Item>() {
					@Override
					public int compare(Item o1, Item o2) {
						return Double.compare(o1.getDistance(), o2.getDistance());
					}
				});
				
				visitedItems.addAll(items);
				
				recommendations.addAll(filteredItems);
			}
		}finally {
			conn.close();
		}
		
		return recommendations;
	}
}
