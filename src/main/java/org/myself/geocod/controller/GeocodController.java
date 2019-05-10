package org.myself.geocod.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.myself.geocod.pojo.GeocodResult;
import org.myself.geocod.pojo.Location;
import org.myself.geocod.service.GeocodService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/geocod")
public class GeocodController {

	private final GeocodService geocodService;
	
	public GeocodController(GeocodService geocodService) {
		this.geocodService = geocodService;
	}
	
	@GetMapping(value = "/address/location", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public GeocodResult getAddressCoords(@RequestParam(required = true) String direction) {
		GeocodResult res = new GeocodResult();
		
		Location loc = this.geocodService.getDirectionCoordinates(direction);
		
		if (loc.getError() != null && loc.getError().isEmpty()) {
			res.setError(loc.getError());
		}
		else {
			res.getLocations().add(loc);
		}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/file/locations", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public GeocodResult getDirectionsFileCoords(@RequestParam(required = true) MultipartFile file) {
		GeocodResult res = new GeocodResult();
		
		Map<String, Object> directions = this.geocodService.getFileDirections(file);
		if (directions.containsKey("error")) {
			res.setError(directions.get("error").toString());
		}
		else if (directions.containsKey("directions")) {
			List<Location> locs = new ArrayList<>();
			
			List<String> dirList = (List<String>)directions.get("directions");
			if (dirList != null && !dirList.isEmpty()) {
				dirList.forEach(direction -> locs.add(this.geocodService.getDirectionCoordinates(direction)));
				
				res.setLocations(locs);
			}
			else {
				res.setError("Directions file is empty");
			}
		}
		else {
			res.setError("Error parsing directions file");
		}
		
		return res;
	}
	
}
