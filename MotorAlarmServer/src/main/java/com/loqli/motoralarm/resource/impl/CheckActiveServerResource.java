package com.loqli.motoralarm.resource.impl;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.loqli.motoralarm.api.ActiveResult;
import com.loqli.motoralarm.dao.DeviceDao;
import com.loqli.motoralarm.model.Device;
import com.loqli.motoralarm.resource.CheckActiveResource;

public class CheckActiveServerResource extends ServerResource implements CheckActiveResource {

	@Inject
	private DeviceDao deviceDao;

	private String id;

	@Override
	public void init(Context context, Request request, Response response) {
		super.init(context, request, response);
		this.id = (String) request.getAttributes().get("id");
	}

	@Override
	@Get
	public ActiveResult checkActive() {

		Device device = deviceDao.getDevice(id);

		return new ActiveResult(id, device.isActive(), -1l);
	}

}
