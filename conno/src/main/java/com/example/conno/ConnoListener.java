package com.example.conno;

import android.content.Intent;

public interface ConnoListener {
    void onConnectivityChanged(Intent intent);
    void onNotifierDetached();
    void onNotifierAttached();


}
