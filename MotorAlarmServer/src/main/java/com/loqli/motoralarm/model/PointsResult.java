package com.loqli.motoralarm.model;

import java.util.List;

import com.loqli.motoralarm.model.Point;

public class PointsResult {

	private int result;

	private List<Point> points;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

}
