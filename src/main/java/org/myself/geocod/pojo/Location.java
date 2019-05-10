package org.myself.geocod.pojo;

public class Location {
	
	private String direction;
	private Coords coordinates;
	private String error;
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Coords getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(Coords coordinates) {
		this.coordinates = coordinates;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}
