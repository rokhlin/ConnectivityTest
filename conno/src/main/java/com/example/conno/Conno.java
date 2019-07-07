package com.example.conno;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.annotation.NonNull;

public class Conno  {
    private static ConnoManager manager;
    @SuppressLint("StaticFieldLeak")
    private static Conno instance = null;

    public static Conno getInstance(@NonNull Application application){
        if(instance == null) instance = new Conno(application);
        return instance;
    }

    public boolean isNetworkAvailable(){
        return manager.isNetworkAvailable();
    }



    public static void registerCallbacks(@NonNull ConnoListener listener){
        manager.registerNotifier(listener);
    }

    public static void unregisterCallbacks(){
        manager.detachNotifier();
    }

    private Conno(Application application){
        manager = new ConnoManager(application);
    }
}
