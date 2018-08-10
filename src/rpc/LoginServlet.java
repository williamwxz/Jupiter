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
    private static final DBConnection connection = DBConnectionFactory.getConnection();
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
	   	 try {
			JSONObject msg = new JSONObject();

			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString(USER_ID);

			HttpSession session = request.getSession();
			if (session.getAttribute(USER) == null) {
				response.setStatus(403);
				msg.put("status", "Session Invalid");
			} else {
				String user = (String) session.getAttribute(USER);
				String name = connection.getFullname(userId);
				msg.put("status", "OK");
				msg.put("user_id", user);
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
	   	 try {
	   		 JSONObject msg = new JSONObject();
	   		 
	   		 String user = request.getParameter("user_id");
	   		 String pwd = request.getParameter("password");
	   		 if (connection.verifyLogin(user, pwd)) {
	   			 HttpSession session = request.getSession();
	   			 session.setAttribute("user", user);
	   			 // setting session to expire in 10 minutes
	   			 session.setMaxInactiveInterval(10 * 60);
	   			 // Get user name
	   			 String name = connection.getFullname(user);
	   			 msg.put("status", "OK");
	   			 msg.put("user_id", user);
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
