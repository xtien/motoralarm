package com.loqli.motoralarm.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.loqli.motoralarm.dao.DeviceDao;
import com.loqli.motoralarm.guice.InjectLogger;
import com.loqli.motoralarm.model.Device;

public class DeviceDaoImpl implements DeviceDao {

	@InjectLogger
	private Log log;

	@Inject
	private EntityManager em;

	@Override
	public void update(String id, boolean activate) {

		Device device = getDevice(id);

		if (device != null) {
			device.setActive(activate);

			em.getTransaction().begin();
			em.merge(device);
			em.getTransaction().commit();

		} else {

			em.getTransaction().begin();
			em.persist(new Device(id, activate));
			em.getTransaction().commit();

		}
	}

	@Override
	public Device getDevice(String deviceId) {

		TypedQuery<Device> query = em.createQuery(
				"select d from Device d where d.id = :id", Device.class);

		Device device = null;

		List<Device> result = query.setParameter("id", deviceId)
				.getResultList();

		if (result != null && result.size() > 0) {
			device = result.get(0);
		}

		return device;
	}

	@Override
	public void create(Device device) {
		
		em.getTransaction().begin();
		em.persist(device);
		em.getTransaction().commit();
	}
}
