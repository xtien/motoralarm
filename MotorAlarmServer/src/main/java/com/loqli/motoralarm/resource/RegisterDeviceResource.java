package com.loqli.motoralarm.resource;

import org.restlet.resource.Post;

import com.loqli.motoralarm.api.ActiveResult;
import com.loqli.motoralarm.model.Device;

public interface RegisterDeviceResource {

	@Post
	public ActiveResult registerDevice(Device device);
}
