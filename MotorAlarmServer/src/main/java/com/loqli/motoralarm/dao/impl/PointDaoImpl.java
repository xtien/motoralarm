package com.loqli.motoralarm.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;

import com.google.inject.Inject;
import com.loqli.motoralarm.model.Point;
import com.loqli.motoralarm.dao.PointDao;
import com.loqli.motoralarm.guice.InjectLogger;

public class PointDaoImpl implements PointDao {

	@InjectLogger
	private Log log;

	@Inject
	private EntityManager em;

	@Override
	public void add(String id, double latitude, double longitude) {

		em.getTransaction().begin();
		em.persist(new Point(id, latitude, longitude, System.currentTimeMillis()));
		em.getTransaction().commit();
	}

	@Override
	public List<Point> getPoints(String id, long period) {

		long time = System.currentTimeMillis() - period;

		TypedQuery<Point> query = em.createQuery(
				"select p from Point p where p.id = :id and p.time > :time ", Point.class);

		List<Point> result = query.setParameter("id", id).setParameter("time", time)
				.getResultList();

		if (result == null || result.size() == 0) {
			result = query.setParameter("id", id).setParameter("time", 3600000l).getResultList();
		}

		return result;
	}
}
