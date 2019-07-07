package com.example.connectivitytest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.conno.Conno
import com.example.conno.ConnoListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ConnoListener {
    private val infoText = MutableLiveData<String>()
    private val infoText2 = MutableLiveData<String>()
    private val TAG = this::class.java.simpleName
    private val disposables = CompositeDisposable()

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        infoText.observe(this, Observer {
            connection_info.text = it
        })

        infoText2.observe(this, Observer {
            connection_info2.text = it
        })

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
       // Conno.registerCallbacks(this) //If you're using callback style
        Conno.networkCallback().subscribe {
            infoText.postValue("Ping returned: $it")
        }.addToDisposables(disposables)
    }

    override fun onStop() {
        super.onStop()
     //   Conno.unregisterCallbacks() //If you're using callback style
        disposables.dispose()
    }
}


fun Disposable.addToDisposables(disposable: CompositeDisposable){
    disposable.add(this)
}