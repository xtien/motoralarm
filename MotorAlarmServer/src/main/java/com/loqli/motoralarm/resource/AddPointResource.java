package com.loqli.motoralarm.resource;

import org.restlet.resource.Post;

import com.loqli.motoralarm.model.Point;

public interface AddPointResource {

	@Post
	public void addPoint(Point point);
}
