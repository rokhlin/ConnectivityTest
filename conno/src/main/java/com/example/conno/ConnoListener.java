package com.example.conno;

public interface ConnoListener {
    void onNotifierDetached();
    void onNotifierAttached();
    void onConnectivityChanged(boolean isOnline);


}
