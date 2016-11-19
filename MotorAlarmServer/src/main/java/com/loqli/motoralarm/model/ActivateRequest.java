package com.loqli.motoralarm.model;

public class ActivateRequest {

	private String id;
	private boolean activate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}

}
