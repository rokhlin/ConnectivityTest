package com.example.conno;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class NetworkUtills {
    static boolean isNetworkAvailable(Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

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

     static void pingTestSite(Callback callback) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://www.google.com")
                .build();
        client.newCall(request).enqueue(callback);
     }

    static boolean pingTestSite() {
        final boolean[] res  = {false};
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://www.google.com")
                .build();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        res[0] = response.isSuccessful();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return res[0];
    }
}
