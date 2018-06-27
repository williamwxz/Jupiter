package rpc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	public static void writeJsonObect(HttpServletResponse response, JSONObject obj) throws IOException{
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
	public static void writeJsonArray(HttpServletResponse response, JSONArray obj) throws IOException{
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
