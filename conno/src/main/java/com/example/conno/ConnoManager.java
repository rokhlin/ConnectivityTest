package com.example.conno;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnoManager implements Application.ActivityLifecycleCallbacks {

    private ConnoListener listener;
    private NetworkReceiver receiver;
    private Activity activity;


    ConnoManager(Application application){
        application.registerActivityLifecycleCallbacks(this);
    }

    void registerNotifier(ConnoListener listener){
        this.listener = listener;
        if(receiver!=null) receiver.updateListener(listener);
        listener.onNotifierAttached();

    }

    private void registerBroadCastReceiver(Activity activity){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.conn.DATA_ACTIVITY_CHANGE");
        receiver = new NetworkReceiver();
        activity.registerReceiver(receiver, filter);
    }

    void detachNotifier(){
        if(listener == null) return;
        listener.onNotifierDetached();
        listener = null;
    }

    boolean isNetworkAvailable() {
        final ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    assert nc != null;
                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }



    // It works only until v23
    boolean ping23(String path){
        String pathToPing = "google.com";
        if(pathIsCorrect(path)) pathToPing = path;

        try {
            return InetAddress.getByName(pathToPing).isSiteLocalAddress();
        } catch (UnknownHostException ignore) {
            ignore.printStackTrace();
            System.out.println(" Exception:"+ignore);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Exception:"+e);
        }
        return false;
    }

    private static boolean pathIsCorrect(String path) {
        return false;//TODO NOT IMPLEMENTED
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        this.activity = activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        registerBroadCastReceiver(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activity.unregisterReceiver(receiver);
        detachNotifier();
        this.activity = null;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}
