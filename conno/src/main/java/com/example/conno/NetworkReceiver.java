package com.example.conno;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkReceiver extends BroadcastReceiver {
    private ConnoListener listener;
    private static final String TAG = NetworkReceiver.class.getName();

    void updateListener(ConnoListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(listener != null) listener.onConnectivityChanged(intent);
    }
}