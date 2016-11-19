package com.loqli.motoralarm.resource.impl;

import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.loqli.motoralarm.model.Point;
import com.loqli.motoralarm.model.PointsResult;
import com.loqli.motoralarm.dao.PointDao;
import com.loqli.motoralarm.model.Point;
import com.loqli.motoralarm.resource.GetPointsResource;

public class GetPointsServerResource extends ServerResource implements GetPointsResource {

	@Inject
	private PointDao pointDao;
	private String id;
	private long time;

	@Override
	public void init(Context context, Request request, Response response) {
		super.init(context, request, response);
		this.id = (String) request.getAttributes().get("id");
		String timeString = (String) request.getAttributes().get("time");
		time = Long.parseLong(timeString);
	}

	@Override
	@Get
	public PointsResult getPoints() {

		PointsResult result = new PointsResult();
		List<Point> points = pointDao.getPoints(id, time);

		if (points != null && points.size() > 0) {
			result.setPoints(points);
			result.setResult(0);
		} else {
			result.setResult(-1);
		}

		return result;
	}

}
