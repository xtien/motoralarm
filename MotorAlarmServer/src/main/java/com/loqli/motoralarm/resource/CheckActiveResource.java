package com.loqli.motoralarm.resource;

import org.restlet.resource.Get;

import com.loqli.motoralarm.api.ActiveResult;

public interface CheckActiveResource {

	@Get
	public ActiveResult checkActive();
}
