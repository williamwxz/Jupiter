package rpc;

import java.io.IOException;
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
 * Servlet implementation class SearchItem
 */
@WebServlet(name = "search", urlPatterns = { "/search" })
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow access only if session exists
//		HttpSession session = request.getSession();
//		if (session.getAttribute("user") == null) {
//			response.setStatus(403);
//			return;
//		}
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String keyword = request.getParameter("term");
		String userId = request.getParameter("user_id");
		
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			List<Item> items = connection.searchItems(lat, lon, keyword);
			Set<String> favoriteItems = connection.getFavoriteItemIds(userId);

			JSONArray itemList = new JSONArray();
			for (Item item:items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoriteItems.contains(item.getItemID()));
				itemList.put(obj);
			}
			RpcHelper.writeJsonArray(response, itemList);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			connection.close();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
