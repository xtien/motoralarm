package com.loqli.motoralarm.robo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.loqli.motoralarm.api.provider.ObjectMapperProvider;
import com.loqli.motoralarm.points.PointsManager;
import com.loqli.motoralarm.points.impl.PointsManagerImpl;
import com.loqli.motoralarm.prefs.MyPreferences;
import com.loqli.motoralarm.prefs.impl.MyPreferencesImpl;

public class MyModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(PointsManager.class).to(PointsManagerImpl.class);
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
        bind(MyPreferences.class).to(MyPreferencesImpl.class);
    }
}
