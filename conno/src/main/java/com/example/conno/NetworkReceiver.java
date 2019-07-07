package com.example.conno;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

public class NetworkReceiver extends BroadcastReceiver {
    private ConnoListener listener;
    private static final String TAG = NetworkReceiver.class.getName();
    public BehaviorSubject<String> subject = BehaviorSubject.create();

    void updateListener(ConnoListener listener){
        this.listener = listener;
    }

    Flowable<String> getObservable(){
        return subject.toFlowable(BackpressureStrategy.LATEST);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null) subject.onNext(action);
        if(listener != null) listener.onConnectivityChanged(intent);
    }
}