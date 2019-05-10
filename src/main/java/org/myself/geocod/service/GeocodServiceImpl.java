package org.myself.geocod.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.myself.geocod.pojo.Coords;
import org.myself.geocod.pojo.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GeocodServiceImpl implements GeocodService {

	@Value("${googleapi.key}")
	private String googleApiKey;
	
	@Value("${googleapi.url}")
	private String googleApiUrl;
	
	@Override
	public Location getDirectionCoordinates(String direction) {
		Location loc = new Location();
		loc.setDirection(direction);
		
		try {
			loc.setCoordinates(this.parseGoogleApiResponse(this.getGoogleApiResponse(direction)));
		}
		catch (Exception e) {
			loc.setError(e.getMessage());
		}
		
		return loc;
	}
	
	@Override
	public Map<String, Object> getFileDirections(MultipartFile file) {
		Map<String, Object> res = new HashMap<>();
		
		try {
			List<String> directions = new ArrayList<>();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			br.lines().forEach(line -> directions.add(line));
			
			res.put("directions", directions);
		}
		catch (Exception e) {
			res.put("error", e.getMessage());
		}
		
		return res;
	}
	
	private JSONObject getGoogleApiResponse(String direction) throws Exception {
		String apiUrl = this.googleApiUrl + "?address=" + direction.replace(" ", "%20") + "&key=" + this.googleApiKey;
		
		URL url = new URL(apiUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Error getting Google Api response");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String resp = br.lines().collect(Collectors.joining());

		conn.disconnect();
		
		if (resp.isBlank()) {
			throw new RuntimeException("Google Api response is empty");
		}

		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(resp);
		
		if (json != null) {
			return json;
		}
		else {
			throw new RuntimeException("Error converting Google Api response to JSON");
		}
	}
	
	private Coords parseGoogleApiResponse(JSONObject json) throws Exception {
		String status = (String)json.get("status");
		if (!"OK".equals(status)) {
			String errorMsg = (String)json.get("error_message");
			
			throw new RuntimeException("Google Api response - " + errorMsg);
		}
		
		JSONArray results = (JSONArray)json.get("results");
		if (results != null && !results.isEmpty()) {
			JSONObject firstRes = (JSONObject)results.get(0);
			JSONObject geometry = (JSONObject)firstRes.get("geometry");
			if (geometry != null) {
				JSONObject location = (JSONObject)geometry.get("location");
				if (location != null) {
					Coords crds = new Coords();

					crds.setLatitude((Double)location.get("lat"));
					crds.setLongitude((Double)location.get("lng"));
					
					return crds;
				}
				else {
					throw new RuntimeException("Google Api response - location element not found");
				}
			}
			else {
				throw new RuntimeException("Google Api response - geometry element not found");
			}
		}
		else {
			throw new RuntimeException("Google Api Response - No results found");
		}
	}
	
}
