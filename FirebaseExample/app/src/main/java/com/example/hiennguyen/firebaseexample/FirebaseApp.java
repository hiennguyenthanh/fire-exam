package com.example.hiennguyen.firebaseexample;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by sphien2011 on 01/03/2017.
 */
public class FirebaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
