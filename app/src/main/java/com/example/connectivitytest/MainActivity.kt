package com.example.connectivitytest

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.conno.Conno
import com.example.conno.ConnoListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ConnoListener {
    private val infoText = MutableLiveData<String>()
    private val infoText2 = MutableLiveData<String>()
    private val TAG = this::class.java.simpleName

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        infoText.observe(this, Observer {
            connection_info.text = it
        })

        infoText2.observe(this, Observer {
            connection_info2.text = it
        })

//        Conno.ping("ABC").let {
//            infoText.postValue("Ping returned: $it")
//        }

        Conno.getInstance(this.application).isNetworkAvailable.let {
            infoText2.postValue("isNetworkAvailable returned: $it")
        }

    }

    override fun onConnectivityChanged(intent: Intent) {
        Log.i(TAG, "onConnectivityChanged: ${intent.action}")
    }

    override fun onNotifierDetached() {
        Log.i(TAG, "onNotifierDetached: ")
    }

    override fun onNotifierAttached() {
        Log.i(TAG, "onNotifierAttached: ")
    }

    override fun onResume() {
        super.onResume()
        Conno.registerCallbacks(this)
    }

    override fun onStop() {
        super.onStop()
        Conno.unregisterCallbacks()
    }
}
