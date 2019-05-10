package org.myself.geocod.service;

import java.util.Map;

import org.myself.geocod.pojo.Location;
import org.springframework.web.multipart.MultipartFile;

public interface GeocodService {

	public Location getDirectionCoordinates(String direction);
	
	public Map<String, Object> getFileDirections(MultipartFile file);
	
}
