package com.loqli.motoralarm.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveResult {

	@JsonProperty("deviceId")
	private String deviceId;
	
	@JsonProperty("active")
	private boolean active;

	@JsonProperty("recheckTime")
	private long recheckTime;

	public ActiveResult(String id, boolean active, long time) {
		this.deviceId = id;
		this.active = active;
		this.recheckTime = time;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getRecheckTime() {
		return recheckTime;
	}

	public void setRecheckTime(long recheckTime) {
		this.recheckTime = recheckTime;
	}
}
