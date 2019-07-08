package com.example.conno;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class Conno  {
    @SuppressLint("StaticFieldLeak")
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

    public static Flowable<Boolean> networkCallback(){
        return Observable.combineLatest(manager.networkCallbacks(), manager.siteIsReachable(), new BiFunction<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean aBoolean, Boolean aBoolean2) {
                return aBoolean && aBoolean2;
            }
        }).toFlowable(BackpressureStrategy.LATEST)

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
