package com.loqli.motoralarm.guice;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.loqli.motoralarm.dao.DeviceDao;
import com.loqli.motoralarm.dao.PointDao;
import com.loqli.motoralarm.dao.impl.DeviceDaoImpl;
import com.loqli.motoralarm.dao.impl.PointDaoImpl;

/**
 * 
 * @author christine
 * 
 */
public class MyModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(PointDao.class).to(PointDaoImpl.class);
		bind(DeviceDao.class).to(DeviceDaoImpl.class);

		// inject logger
		// (http://code.google.com/p/google-guice/wiki/CustomInjections)
		bindListener(Matchers.any(), new LogTypeListener());
	}
}
