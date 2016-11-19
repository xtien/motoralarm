package com.loqli.motoralarm.resource;

import org.restlet.resource.Get;

import com.loqli.motoralarm.model.PointsResult;

public interface GetPointsResource {

	@Get
	public PointsResult getPoints();
}
