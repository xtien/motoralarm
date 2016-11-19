package com.loqli.motoralarm.resource.impl;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.loqli.motoralarm.model.Point;
import com.loqli.motoralarm.dao.PointDao;
import com.loqli.motoralarm.model.Point;
import com.loqli.motoralarm.resource.AddPointResource;

public class AddPointServerResource extends ServerResource implements AddPointResource {

	@Inject
	private PointDao pointsDao;

	@Override
	@Post
	public void addPoint(Point point) {

		pointsDao.add(point.getId(), point.getLatitude(), point.getLongitude());
	}
}
