package com.example.conno;

import android.app.Activity;
import android.app.Application;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ConnoManager implements Application.ActivityLifecycleCallbacks {

    private ConnoListener listener;
    private NetworkReceiver receiver;
    private Activity activity;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    ConnoManager(Application application){
        application.registerActivityLifecycleCallbacks(this);
    }

    private void registerBroadCastReceiver(Activity activity){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        receiver = new NetworkReceiver();
        activity.registerReceiver(receiver, filter);
    }

    private Consumer<Boolean> notifyConnectivityChanged = new Consumer<Boolean>() {
        @Override
        public void accept(Boolean isOnline) {
            System.out.println("onNext: isOnline: "+ isOnline);
            listener.onConnectivityChanged(isOnline);
        }
    };

    private void subscribeToConnectivityChanges() {
        Disposable d = networkNotifier()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notifyConnectivityChanged);
        compositeDisposable.add(d);
    }

    void registerNotifier(ConnoListener listener){
        this.listener = listener;
        listener.onNotifierAttached();
        subscribeToConnectivityChanges();
    }

    void detachNotifier(){
        if(listener == null) return;
        listener.onNotifierDetached();
        listener = null;
        compositeDisposable.clear();
    }

    private Observable<Boolean> checkActualConnectivity(){
        return Observable.combineLatest(networkIsAvailable(), siteIsReachable(),
                new BiFunction<Boolean, Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean, Boolean aBoolean2) {
                        return aBoolean && aBoolean2;
                    }
                });
    }

    Flowable<Boolean> networkNotifier(){
        return receiver.getConnectivityStateObservable()
                .switchMap(new Function<String, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(String s){
                        return checkActualConnectivity();
                    }
                })
                .toFlowable(BackpressureStrategy.LATEST);

    }

    boolean isNetworkAvailable(){
        return NetworkUtills.isNetworkAvailable(activity) && NetworkUtills.pingTestSite();
    }

    private Observable<Boolean> networkIsAvailable(){
        return Observable.just(NetworkUtills.isNetworkAvailable(activity));
    }

    private Observable<Boolean> siteIsReachable(){
        final BehaviorSubject<Boolean> subject = BehaviorSubject.create();
        NetworkUtills.pingTestSite(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("onFailure: "+e.getMessage());
                subject.onNext(false);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                System.out.println("onResponse: "+ response.toString());
                subject.onNext(response.isSuccessful());
            }
        });
        return subject;
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
    public void onActivityPaused(@NonNull Activity activity) { }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activity.unregisterReceiver(receiver);
        this.activity = null;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) { }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        detachNotifier();
    }
}