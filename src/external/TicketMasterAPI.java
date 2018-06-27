package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TicketMasterAPI {
	private static final String API_URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "YL4hKDs92DkA89RFmN48jFyX9RTFW7fA";
	private static final String EMBEDDED = "_embedded";
	private static final String EVENTS = "events";
	private static final int RADIUS = 50;
	
	public JSONArray EventSearch(double lat, double lon, String keyword) {
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
				return new JSONArray();
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
				return new JSONArray();
			}
			JSONObject embedded = responseObj.getJSONObject(EMBEDDED);
			JSONArray events = embedded.getJSONArray(EVENTS);
			
			return events;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}
	
	private void queryAPI(double lat, double lon) {
		JSONArray events = EventSearch(lat, lon, null);
		try {
			for (int i=0; i<events.length(); i++) {
				JSONObject event = events.getJSONObject(i);
				System.out.println(event);
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		tmAPI.queryAPI(29.682684, -95.295410);
	}
}
