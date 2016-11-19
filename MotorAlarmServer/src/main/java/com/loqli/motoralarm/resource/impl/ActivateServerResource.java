package com.loqli.motoralarm.resource.impl;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.loqli.motoralarm.dao.DeviceDao;
import com.loqli.motoralarm.model.ActivateRequest;
import com.loqli.motoralarm.resource.ActivateResource;

public class ActivateServerResource extends ServerResource implements ActivateResource {

	@Inject
	private DeviceDao deviceDao;

	@Override
	@Post
	public void activate(ActivateRequest activateRequest) {
		deviceDao.update(activateRequest.getId(), activateRequest.isActivate());
	}

}
