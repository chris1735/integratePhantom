package phantom.basics;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * Copyright (C) 湖北无垠智探科技发展有限公司
 * Author: zuoz
 * Date: 2020/10/30 18:39
 * Description:
 * History:
 */
public class PreviewActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("*********  " + getClass().getSimpleName() + ".onCreate  *********");
        super.onCreate(savedInstanceState);

        // When the compile and target version is higher than 22, please request the following permission at runtime to ensure the SDK works well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        setContentView(R.layout.activity_example);

        textView = findViewById(R.id.textView);
    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("*********  " + getClass().getSimpleName() + ".onStart  *********");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("*********  " + getClass().getSimpleName() + ".onRestoreInstanceState  *********");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("*********  " + getClass().getSimpleName() + ".onRestart  *********");

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("*********  " + getClass().getSimpleName() + ".onResume  *********");

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("*********  " + getClass().getSimpleName() + ".onPause  *********");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("*********  " + getClass().getSimpleName() + ".onBackPressed  *********");
    }


    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("*********  " + getClass().getSimpleName() + ".onStop  *********");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("*********  " + getClass().getSimpleName() + ".onSaveInstanceState  *********");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("*********  " + getClass().getSimpleName() + ".onDestroy  *********");

    }


    public void start(View view) {
        System.out.println("~~button.start~~");
        startSDKRegistration();

    }


    public void stop(View view) {
        System.out.println("~~button.stop~~");



    }

    public void bind(View view) {
        System.out.println("~~button.bind~~");

    }

    public void unbind(View view) {
        System.out.println("~~button.unbind~~");
    }

    public void reloading(View view) {
        System.out.println("~~button.reloading~~");

    }


    public void del(View view) {
        System.out.println("~~button.del~~");



    }


    public void query(View view) {
        System.out.println("~~button.query~~");

    }


    private void startSDKRegistration() {
        System.out.println("~~~~  " + getClass().getSimpleName() + ".startSDKRegistration  ~~~~");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                log("registering, pls wait...");

                log("registerApp start");
                DJISDKManager.getInstance().registerApp(PreviewActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                    @Override
                    public void onRegister(DJIError djiError) {
                        log("~~SDKManagerCallback.onRegister~~");
                        if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                            log("Register Success");
                            DJISDKManager.getInstance().startConnectionToProduct();
                        } else {
                            log("Register sdk fails, please check the bundle id and network connection!");
                        }
                        log(djiError.getDescription());
                    }

                    @Override
                    public void onProductDisconnect() {
                        log("~~SDKManagerCallback.onProductDisconnect~~");
                        log("Product Disconnected");


                    }
                    @Override
                    public void onProductConnect(BaseProduct baseProduct) {
                        log("~~SDKManagerCallback.onProductConnect~~");
                        log(String.format("onProductConnect newProduct:%s", baseProduct));
                        log("Product Connected");


                    }

                    @Override
                    public void onProductChanged(BaseProduct baseProduct) {
                        log("~~SDKManagerCallback.onProductChanged~~");

                    }

                    @Override
                    public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent, BaseComponent newComponent) {
                        log("~~SDKManagerCallback.onComponentChange~~");

                        if (newComponent != null) {
                            newComponent.setComponentListener(new BaseComponent.ComponentListener() {

                                @Override
                                public void onConnectivityChange(boolean isConnected) {
                                    log("~~ComponentListener.onConnectivityChange~~");
                                    log("onComponentConnectivityChanged: " + isConnected);

                                }
                            });
                        }
                        log(String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                componentKey,
                                oldComponent,
                                newComponent));

                    }

                    @Override
                    public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {
                        log("~~SDKManagerCallback.onInitProcess~~");

                    }

                    @Override
                    public void onDatabaseDownloadProgress(long l, long l1) {
                        log("~~SDKManagerCallback.onDatabaseDownloadProgress~~");

                    }

                });
            }
        });
    }

    private void log(String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("LOG|" + msg);
                textView.setText(textView.getText() + "\n" + msg);
            }
        });
    }

}
