package reporting;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import game.GameState;

public class Reporter {

	private class Response {
		public boolean error;
		public MatchReport game;
		public Response(boolean error, MatchReport game) {
			super();
			this.error = error;
			this.game = game;
		}
	}
	
	private class MatchReport {
		public String _id;
		public String map;
		public String ai;
		public int timebudget;
		public String winner;
		public int turns;
		public int heuristic;
		public int processors;
		public String start;
		public String end;
		public int level;
		public String __v;
		public MatchReport(String _id, String map, String ai,
				int timebudget, String winner, int turns, int heuristic, 
				int processors, String start, String end, int level, String __v) {
			super();
			this._id = _id;
			this.map = map;
			this.ai = ai;
			this.timebudget = timebudget;
			this.heuristic = heuristic;
			this.winner = winner;
			this.turns = turns;
			this.processors = processors;
			this.start = start;
			this.end = end;
			this.level = level;
			this.__v = __v;
		}
	}

	private final String USER_AGENT = "Mozilla/5.0";
	
	String create = "http://hero-academy.herokuapp.com/game/create";
	String update = "http://hero-academy.herokuapp.com/game/update";
	
	public String id;

	public Reporter() {
		super();
	}
	
	public void updateReport(int turns, double heuristic, String winner) {
		
		try {
			URL url = new URL(update);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", id);
			params.put("turns", turns);
			params.put("heuristic", heuristic);
			params.put("winner", winner);
			String response = post(url, params);
			Gson gson = new Gson();
			//Response res = gson.fromJson(response, Response.class);
			MatchReport report = gson.fromJson(response, MatchReport.class);
			if (report == null || report._id == null || report._id == "")
				System.out.println("Error");
			else
				id = report._id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void createReport(String mapName, String ai, int timeBudget, int level) {
		
		try {
			int processors = Runtime.getRuntime().availableProcessors();
			URL url = new URL(create);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("map", mapName);
			params.put("ai", ai);
			params.put("timebudget", ""+timeBudget);
			params.put("processors", ""+processors);
			params.put("level", level);
			String response = post(url, params);
			Gson gson = new Gson();
			//Response res = gson.fromJson(response, Response.class);
			MatchReport report = gson.fromJson(response, MatchReport.class);
			if (report == null || report._id == null || report._id == "")
				System.out.println("Error");
			else
				id = report._id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// HTTP POST request
	private String post(URL url, Map<String, Object> params) throws Exception {
 
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		String urlParameters = "";
		for(String key : params.keySet()){
			if (!urlParameters.equals(""))
				urlParameters += "&";
			urlParameters += key + "=" + params.get(key);
		} 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
		return response.toString();
	}	
	
}
