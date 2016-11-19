package com.loqli.motortracker.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveResult {

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("recheckTime")
    private long recheckTime;

    public boolean isActive() {
		return active;
	}

}
