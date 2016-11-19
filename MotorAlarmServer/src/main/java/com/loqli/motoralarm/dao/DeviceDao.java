package com.loqli.motoralarm.dao;

import com.loqli.motoralarm.model.Device;

public interface DeviceDao {

	public void update(String id, boolean activate);

	public Device getDevice(String deviceId);

	public void create(Device device);
}
