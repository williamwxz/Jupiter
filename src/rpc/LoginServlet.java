package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final String USER_ID = "user_id";
    private static final String USER = "user";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
	   	 try {
			JSONObject msg = new JSONObject();

			HttpSession session = request.getSession(false);
			if (session == null) {
				response.setStatus(403);
				msg.put("status", "Session Invalid");
			} else {
				String user_id = (String) session.getAttribute(USER_ID);
				String name = conn.getFullname(user_id);
				msg.put("status", "OK");
				msg.put("user_id", user_id);
				msg.put("name", name);
			}
			RpcHelper.writeJsonObect(response, msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
	   	 try {
	   		 JSONObject input = RpcHelper.readJSONObject(request);
	   		 String userID = input.getString(USER_ID);
	   		 String pwd = input.getString("password");
	   		 
	   		 JSONObject msg = new JSONObject();
	   		 
	   		 if (conn.verifyLogin(userID, pwd)) {
	   			 System.out.println("Valid user: "+userID);
	   			 HttpSession session = request.getSession();
	   			 session.setAttribute(USER_ID, userID);
	   			 // setting session to expire in 10 minutes
	   			 session.setMaxInactiveInterval(10 * 60);
	   			 // Get user name
	   			 String name = conn.getFullname(userID);
	   			 msg.put("status", "OK");
	   			 msg.put("user_id", userID);
	   			 msg.put("name", name);
	   		 } else {
	   			 response.setStatus(401);
	   		 }
	   		 RpcHelper.writeJsonObect(response, msg);
	   	 } catch (JSONException e) {
	   		 e.printStackTrace();
	   	 }
	}

}
