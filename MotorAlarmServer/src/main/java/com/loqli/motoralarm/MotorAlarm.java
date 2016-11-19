package com.loqli.motoralarm;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.ext.guice.FinderFactory;
import org.restlet.ext.guice.RestletGuice;
import org.restlet.routing.Router;

import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.loqli.motoralarm.guice.MyModule;
import com.loqli.motoralarm.persist.PersistenceInitializer;
import com.loqli.motoralarm.resource.impl.ActivateServerResource;
import com.loqli.motoralarm.resource.impl.AddPointServerResource;
import com.loqli.motoralarm.resource.impl.CheckActiveServerResource;
import com.loqli.motoralarm.resource.impl.GetPointsServerResource;
import com.loqli.motoralarm.resource.impl.RegisterDeviceServerResource;

public class MotorAlarm {

	public void startIt() throws Exception {

		Injector injector = RestletGuice.createInjector(new MyModule(), new JpaPersistModule(
				"motoralarm-db"));
		injector.getInstance(PersistenceInitializer.class);

		FinderFactory ff = injector.getInstance(FinderFactory.class);

		/*
		 * main server
		 */
		final Component mainComponent = new Component();
		mainComponent.getServers().add(Protocol.HTTP, 9010);

		final Router router = new Router(mainComponent.getContext().createChildContext());

		router.attach("addPoint/", ff.finder(AddPointServerResource.class));
		router.attach("getPoints/{id}", ff.finder(GetPointsServerResource.class));
		router.attach("getPoints/{id}/interval/{time}", ff.finder(GetPointsServerResource.class));
		router.attach("checkActive/{id}", ff.finder(CheckActiveServerResource.class));
		router.attach("registerDevice/", ff.finder(RegisterDeviceServerResource.class));
		router.attach("activate/", ff.finder(ActivateServerResource.class));

		mainComponent.getDefaultHost().attach("/motoralarm/", router);
		mainComponent.start();
	}
}
