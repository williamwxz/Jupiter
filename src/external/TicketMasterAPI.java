package external;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterAPI {
	private static final String API_URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "YL4hKDs92DkA89RFmN48jFyX9RTFW7fA";
	private static final int RADIUS = 50;
	
	private static final String EMBEDDED = "_embedded";
	private static final String VENUES = "venues";
	private static final String EVENTS = "events";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String EVENT_URL = "url";
	private static final String RATING = "rating";
	private static final String DISTANCE = "distance";
	private static final String ADDRESS = "address";
	private static final String LINE1 = "line1";
	private static final String LINE2 = "line2";
	private static final String LINE3 = "line3";
	private static final String CITY = "city";
	
	private static final String IMAGES = "images";
	
	private static final String CLASSIFICATIONS = "classifications";
	private static final String SEGMENT = "segment";
	
	public List<Item> EventSearch(double lat, double lon, String keyword) {
		if (keyword==null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		}catch (Exception e){
			e.printStackTrace();
		}
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		String requestQuery = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, RADIUS);
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(API_URL+"?"+requestQuery).openConnection();
			int responseCode = connection.getResponseCode();
			System.out.println("Requesting: "+requestQuery);
			System.out.println("Response: "+responseCode);
			if (responseCode!=200) {
				return new ArrayList<>();
			}
			
			StringBuilder responseString = new StringBuilder();
			
			try(BufferedReader inBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
				String inputLine;
				while ((inputLine = inBuffer.readLine())!=null) {
					responseString.append(inputLine);
				}
			}
			JSONObject responseObj = new JSONObject(responseString.toString());
			
			if (responseObj.isNull(EMBEDDED)) {
				return new ArrayList<>();
			}
			JSONObject embedded = responseObj.getJSONObject(EMBEDDED);
			JSONArray events = embedded.getJSONArray(EVENTS);
			
			return getItemList(events);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	private void queryAPI(double lat, double lon) {
		List<Item> events = EventSearch(lat, lon, null);
		try {
			for (Item i:events) {
				JSONObject obj = i.toJSONObject();
				System.out.println(obj);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull(EMBEDDED)) {
			JSONObject embedded = event.getJSONObject(EMBEDDED);
			if (!embedded.isNull(VENUES)) {
				JSONArray venues = embedded.getJSONArray(VENUES);
				for (int i=0; i<venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					
					StringBuilder editor = new StringBuilder();
					if (!venue.isNull(ADDRESS)) {
						JSONObject address = venue.getJSONObject(ADDRESS);
						if (!address.isNull(LINE1)) {
							editor.append(address.getString(LINE1));
							editor.append('\n');
						}
						if (!address.isNull(LINE2)) {
							editor.append(address.getString(LINE2));
							editor.append('\n');
						}
						if (!address.isNull(LINE3)) {
							editor.append(address.getString(LINE3));
							editor.append('\n');
						}
					}
					if (!venue.isNull(CITY)) {
						JSONObject city = venue.getJSONObject(CITY);
						
						if (!city.isNull(NAME)) {
							editor.append(city.getString(NAME));
							editor.append('\n');
						}
					}
					//check if there is address
					String addr = editor.toString();
					if (addr.length()>0) {
						return addr;
					}
				}
			}
		}
		return "";
	}


	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull(IMAGES)) {
			JSONArray images = event.getJSONArray(IMAGES);
			for (int i=0; i<images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				if (!image.equals(EVENT_URL)) {
					return image.getString(EVENT_URL);
				}
			}
		}
		return "";
	}

	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull(CLASSIFICATIONS)) {
			JSONArray classifications = event.getJSONArray(CLASSIFICATIONS);
			for (int i=0; i<classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull(SEGMENT)) {
					JSONObject segment = classification.getJSONObject(SEGMENT);
					if (!segment.isNull(NAME)) {
						categories.add(segment.getString(NAME));
					}
				}
			}
		}
		return categories;
	}

	private List<Item> getItemList(JSONArray events) throws JSONException{
		List<Item> itemList = new ArrayList<>();
		
		for (int i =0; i<events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull(NAME)) {
				builder.setName(event.getString(NAME));
			}
			if (!event.isNull(ID)) {
				builder.setItemID(event.getString(ID));
			}
			if (!event.isNull(EVENT_URL)) {
				builder.setUrl(event.getString(EVENT_URL));
			}
			if (!event.isNull(RATING)) {
				builder.setRating(event.getDouble(RATING));
			}
			if (!event.isNull(DISTANCE)) {
				builder.setDistance(event.getDouble(DISTANCE));
			}
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageURL(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	public static void main(String[] args) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		tmAPI.queryAPI(29.682684, -95.295410);
	}
}
