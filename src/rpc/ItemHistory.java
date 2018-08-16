package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private static final String USER_ID = "user_id";
	private static final String FAVORITE = "favorite";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// invalidate the session if exists
		HttpSession session = request.getSession();
		if (session == null) {
			response.setStatus(403);
			return;
		}
		String userID = session.getAttribute(USER_ID).toString();
		JSONArray array = new JSONArray();
		
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			Set<Item> items = conn.getFavoriteItems(userID);
			for (Item item:items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", true);
				
				array.put(obj);
			}
		}catch (Exception e) {
			
		}finally {
			conn.close();
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// invalidate the session if exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		String userID = session.getAttribute(USER_ID).toString();
		
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			
			JSONArray array = input.getJSONArray(FAVORITE);
			List<String> itemIDs = new ArrayList<>();
			for (int i=0; i<array.length(); i++) {
				itemIDs.add(array.getString(i));
			}
			conn.setFavoriteItems(userID, itemIDs);
			
			RpcHelper.writeJsonObect(response, new JSONObject().put("result", "success"));
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		String userId = session.getAttribute(USER_ID).toString();
		
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			
			JSONArray array = input.getJSONArray(FAVORITE);
			List<String> itemIDs = new ArrayList<>();
			for (int i=0; i<array.length(); i++) {
				itemIDs.add(array.getString(i));
			}
			
			conn.unsetFavoriteItems(userId, itemIDs);
			
			RpcHelper.writeJsonObect(response, new JSONObject().put("result", "success"));
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			conn.close();
		}
	}

}
