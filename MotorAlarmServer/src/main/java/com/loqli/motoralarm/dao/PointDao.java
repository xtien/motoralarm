package com.loqli.motoralarm.dao;

import java.util.List;

import com.loqli.motoralarm.model.Point;

public interface PointDao {

	void add(String id, double latitude, double longitude);

	List<Point> getPoints(String id, long period);

}
