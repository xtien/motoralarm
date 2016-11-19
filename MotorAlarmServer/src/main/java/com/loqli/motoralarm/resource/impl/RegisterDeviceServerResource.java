package com.loqli.motoralarm.resource.impl;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.inject.Inject;
import com.loqli.motoralarm.api.ActiveResult;
import com.loqli.motoralarm.dao.DeviceDao;
import com.loqli.motoralarm.model.Device;
import com.loqli.motoralarm.resource.RegisterDeviceResource;

public class RegisterDeviceServerResource extends ServerResource implements
		RegisterDeviceResource {

	@Inject
	private DeviceDao deviceDao;

	@Override
	public void init(Context context, Request request, Response response) {
		super.init(context, request, response);
	}

	@Override
	@Post
	public ActiveResult registerDevice(Device device) {

		Device d = deviceDao.getDevice(device.getId());

		if (d == null && device != null) {
			deviceDao.create(device);
		}

		ActiveResult result = new ActiveResult(device.getId(), device.isActive(), -1l);
		return result;
	}

}
