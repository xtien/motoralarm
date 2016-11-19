package com.loqli.motoralarm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "device")
public class Device {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	@org.codehaus.jackson.annotate.JsonIgnore
	private int _id;

	@Column(name = "id")
	@JsonProperty("id")
	private String id;

	@Column(name = "active")
	@JsonProperty("active")
	private boolean active;

	public Device(){
		
	}

	public Device(String id, boolean active) {
		this.id = id;
		this.active = active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
