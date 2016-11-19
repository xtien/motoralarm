package com.loqli.motoralarm.model;

public class Point {

	private int _id;

	private double latitude;

	private double longitude;

	private long time;

	private String id;

	public Point() {

	}

	public Point(String id, double latitude, double longitude, long time) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
		this.setId(id);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
