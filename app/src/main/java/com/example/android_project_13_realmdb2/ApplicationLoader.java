package com.example.android_project_13_realmdb2;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationLoader extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("mySql")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
