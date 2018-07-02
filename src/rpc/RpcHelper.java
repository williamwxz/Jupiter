package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	public static void writeJsonObect(HttpServletResponse response, JSONObject obj) throws IOException{
		PrintWriter out = response.getWriter();
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			out.println(obj);
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
			out.println(obj);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
	
	// Parse request from client
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder editor = new StringBuilder();
		try (BufferedReader reader = request.getReader()){
			String line = null;
			while ((line=reader.readLine())!=null) {
				editor.append(line);
			}
			return new JSONObject(editor.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
}
