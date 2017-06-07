package com.orsys.ballard.addclient;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Administrateur on 06/06/2017.
 */

public class GestionClientApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
