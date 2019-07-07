package com.example.connectivitytest;

import android.app.Application;
import com.example.conno.Conno;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Conno.getInstance(this);
    }
}
