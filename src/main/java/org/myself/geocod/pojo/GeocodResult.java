package org.myself.geocod.pojo;

import java.util.ArrayList;
import java.util.List;

public class GeocodResult {

	private String error;
	private List<Location> locations;
	
	public GeocodResult() {
		this.locations = new ArrayList<>();
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	
}
