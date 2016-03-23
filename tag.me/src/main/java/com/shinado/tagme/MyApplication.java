package com.shinado.tagme;

import com.shinado.tagme.database.DBHandler;

import framework.core.Configurations;
import framework.core.Jujuj;
import jujuj.shinado.com.dependency.DefaultApplication;
import provider.CacheProvider;
import provider.ConfigBuilder;
import provider.ImageProvider;
import provider.database.DBProvider;
import provider.volley.VolleyProvider;

public class MyApplication extends DefaultApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        DBProvider dbProvider = new DBProvider();
        dbProvider.setDBHandler(new DBHandler());
        Configurations configurations = new Configurations.Builder()
                .setImageProvider(new ImageProvider())
                .addDataProvider(new CacheProvider())
                .addDataProvider(dbProvider)
                .addDataProvider(new VolleyProvider())
                .build();
        Jujuj.getInstance().init(configurations);
    }
}
