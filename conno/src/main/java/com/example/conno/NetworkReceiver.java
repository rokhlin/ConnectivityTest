package com.example.conno;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class NetworkReceiver extends BroadcastReceiver {
    private BehaviorSubject<String> subject = BehaviorSubject.create();

    Observable<String> getConnectivityStateObservable(){
        return subject;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("onReceive: "+intent.getAction());
        String action = intent.getAction();
        if(action != null) subject.onNext(action);
    }
}