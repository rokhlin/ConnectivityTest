package com.example.conno;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Conno  {
    @SuppressLint("StaticFieldLeak")
    private static ConnoManager manager;
    @SuppressLint("StaticFieldLeak")
    private static Conno instance = null;

    public static Conno init(@NonNull Application application){
        if(instance == null) instance = new Conno(application);
        return instance;
    }

    public static boolean isNetworkAvailable(){
        return manager.isNetworkAvailable();
    }

    public static Flowable<Boolean> connectionNotifier(){
        return manager.networkNotifier()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread());
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
