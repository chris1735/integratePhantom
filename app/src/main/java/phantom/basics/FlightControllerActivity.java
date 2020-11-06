package phantom.basics;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Map;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.gimbal.CapabilityKey;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.GimbalState;
import dji.common.gimbal.Rotation;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.common.util.DJIParamCapability;
import dji.mop.common.Pipelines;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;

import static dji.common.gimbal.Axis.PITCH;
import static dji.common.gimbal.Axis.YAW;
import static dji.common.gimbal.ResetDirection.CENTER;
import static dji.common.gimbal.RotationMode.RELATIVE_ANGLE;

/**
 * Copyright (C) 湖北无垠智探科技发展有限公司
 * Author: zuoz
 * Date: 2020/12/5 11:38
 * Description:
 * History:
 */
public class FlightControllerActivity extends AppCompatActivity {

    private TextView textView;
    private TextureView textureView;
    private BaseProduct baseProduct;
    private DJICodecManager mCodecManager;


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

        setContentView(R.layout.activity_flight_controller);
        textView = findViewById(R.id.textView);
        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                log("~~SurfaceTextureListener.onSurfaceTextureAvailable~~");
                log("surface is " + surface);
                log("width is " + width);
                log("height is " + height);

                startSDKRegistration();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                log("~~SurfaceTextureListener.onSurfaceTextureSizeChanged~~");

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                log("~~SurfaceTextureListener.onSurfaceTextureDestroyed~~");


                if (mCodecManager != null) {
                    mCodecManager.cleanSurface();
                    mCodecManager = null;
                }

                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//                log("~~SurfaceTextureListener.onSurfaceTextureUpdated~~");

            }
        });


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

        if (VideoFeeder.getInstance() != null) {
            VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(null);
            VideoFeeder.getInstance().getPrimaryVideoFeed().destroy();
            VideoFeeder.getInstance().destroy();
        }
    }


    public void start(View view) {
        System.out.println("~~button.start~~");
        startTakeoff();
    }


    public void stop(View view) {
        System.out.println("~~button.stop~~");
        startLanding();
    }


    public void bind(View view) {
        System.out.println("~~button.bind~~");
        turnOffMotors();
    }

    public void unbind(View view) {
        System.out.println("~~button.unbind~~");
        setVirtualStickModeEnabled();
    }

    public void reset(View view) {
        System.out.println("~~button.reset~~");
    }


    public void pitch(View view) {
        System.out.println("~~button.pitch~~");

    }

    public void roll(View view) {
        System.out.println("~~button.roll~~");

    }


    public void yaw(View view) {
        System.out.println("~~button.yaw~~");

    }


    public void query(View view) {
        System.out.println("~~button.query~~");
        setState();
    }


    public void up(View view) {
        System.out.println("~~button.up~~");
        sendVirtualStickFlightControlData(20f, 0f, 0f, 0f);
    }

    public void down(View view) {
        System.out.println("~~button.down~~");
        sendVirtualStickFlightControlData(-20f, 0f, 0f, 0f);

    }

    public void left(View view) {
        System.out.println("~~button.left~~");
        sendVirtualStickFlightControlData(0f, 20f, 0f, 0f);
    }

    public void right(View view) {
        System.out.println("~~button.right~~");
        sendVirtualStickFlightControlData(0f, -20f, 0f, 0f);
    }

    public void rotate(View view) {
        System.out.println("~~button.rotate~~");
        sendVirtualStickFlightControlData(0f, 0f, 20f, 0f);
    }

    public void lift(View view) {
        System.out.println("~~button.lift~~");
        sendVirtualStickFlightControlData(0f, 0f, 0f, 20f);
    }

    public void drop(View view) {
        System.out.println("~~button.drop~~");
        sendVirtualStickFlightControlData(0f, 0f, 0f, -20f);
    }


    private void preview() {
        if (!baseProduct.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {

            VideoFeeder.getInstance()
                    .getPrimaryVideoFeed()
                    .addVideoDataListener(new VideoFeeder.VideoDataListener() {
                        @Override
                        public void onReceive(byte[] bytes, int i) {
//                            log("~~addVideoDataListener.onReceive~~");
//                            log("bytes is " + bytes.length);
//                            log("i is " + i);

                            if (mCodecManager == null) {
                                mCodecManager = new DJICodecManager(FlightControllerActivity.this, textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
                                log("mCodecManager is " + mCodecManager);
                            } else {
                                mCodecManager.sendDataToDecoder(bytes, i);
                            }

                        }
                    });
        }
    }


    private void startSDKRegistration() {
        System.out.println("~~~~  " + getClass().getSimpleName() + ".startSDKRegistration  ~~~~");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                log("registering, pls wait...");

                log("registerApp start");
                DJISDKManager.getInstance().registerApp(FlightControllerActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
                    @Override
                    public void onRegister(DJIError djiError) {
                        log("~~SDKManagerCallback.onRegister~~");
                        if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                            log("Register Success, and starting connection to product");
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

                        if (null != baseProduct && baseProduct.isConnected()) {
                            log("refreshSDK: True");
                            String str = baseProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
                            log("Status: " + str + " connected");
                            FlightControllerActivity.this.baseProduct = baseProduct;
                            preview();

                        } else {
                            log("refreshSDK: False");
                        }

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
//                textView.setText(textView.getText() + "\n" + msg);
            }
        });
    }


    private void rotate(float pitch, float yaw) {
        Gimbal gimbal = baseProduct.getGimbal();
        if (gimbal == null) return;

        Rotation rotation = new Rotation.Builder()
                .pitch(pitch)
//                .roll(roll)
                .yaw(yaw)
                .mode(RELATIVE_ANGLE)
//                .mode(ABSOLUTE_ANGLE)
                .build();
        gimbal.rotate(rotation, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                System.out.println("~~rotate.onResult~~");
                if (djiError == null) {
                    System.out.println("rotate Succeeded");
                } else {
                    System.out.println(djiError.getDescription());
                }
            }
        });

//        gimbal.fineTuneRollInDegrees(roll +=1.0f, new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//                System.out.println("~~rotate.onResult~~");
//                if (djiError == null) {
//                    System.out.println("rotate Succeeded");
//                } else {
//                    System.out.println(djiError.getDescription());
//                }
//            }
//        });


    }


    private void reset() {
    }


    private void setMode() {
        Gimbal gimbal = baseProduct.getGimbal();
        if (gimbal == null) return;

        gimbal.setMode(GimbalMode.FREE, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                System.out.println("~~setMode.onResult~~");
                if (djiError == null) {
                    System.out.println("setMode Succeeded");
                } else {
                    System.out.println(djiError.getDescription());
                }
            }
        });
    }

    private void setMotorEnabled() {
        Gimbal gimbal = baseProduct.getGimbal();
        if (gimbal == null) return;

        gimbal.setMotorEnabled(true, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                System.out.println("~~setMotorEnabled.onResult~~");
                if (djiError == null) {
                    System.out.println("setMotorEnabled Succeeded");
                } else {
                    System.out.println(djiError.getDescription());
                }
            }
        });
    }


    private void getCapabilities() {
        Gimbal gimbal = baseProduct.getGimbal();
        if (gimbal == null) return;

        Map<CapabilityKey, DJIParamCapability> map = gimbal.getCapabilities();
        System.out.println(map);
    }


    private void setState() {

        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        FlightController flightController = aircraft.getFlightController();

        FlightControllerState state = flightController.getState();
        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append("state is " + state);
        stringBuffer.append("\ngetAircraftLocation: " + state.getAircraftLocation());
        stringBuffer.append("\ngetTakeoffLocationAltitude: " + state.getTakeoffLocationAltitude());
        stringBuffer.append("\ngetAttitude: " + "{pitch:" + state.getAttitude().pitch + ", " +
                "roll:" + state.getAttitude().roll + ", " +
                "yaw:" + state.getAttitude().yaw + "}");
        stringBuffer.append("\ngetVelocityX: " + state.getVelocityX());
        stringBuffer.append("\ngetVelocityY: " + state.getVelocityY());
        stringBuffer.append("\ngetVelocityZ: " + state.getVelocityZ());
        stringBuffer.append("\ngetFlightTimeInSeconds: " + state.getFlightTimeInSeconds());
        stringBuffer.append("\ngetFlightMode: " + state.getFlightMode());
        stringBuffer.append("\ngetFlightModeString: " + state.getFlightModeString());
        stringBuffer.append("\ngetSatelliteCount: " + state.getSatelliteCount());
        stringBuffer.append("\ngetGPSSignalLevel: " + state.getGPSSignalLevel());
        stringBuffer.append("\ngetUltrasonicHeightInMeters: " + state.getUltrasonicHeightInMeters());
        stringBuffer.append("\ngetOrientationMode: " + state.getOrientationMode());
        stringBuffer.append("\ngetBatteryThresholdBehavior: " + state.getBatteryThresholdBehavior());
        stringBuffer.append("\ngetFlightWindWarning: " + state.getFlightWindWarning());
        stringBuffer.append("\ngetFlightCount: " + state.getFlightCount());
        stringBuffer.append("\ngetFlightLogIndex: " + state.getFlightLogIndex());
//        stringBuffer.append("\ngetHomeLocation: " + state.getHomeLocation());
//        stringBuffer.append("\ngetGoHomeAssessment: " + state.getGoHomeAssessment());
        stringBuffer.append("\ngetGoHomeExecutionState: " + state.getGoHomeExecutionState());
        stringBuffer.append("\ngetGoHomeHeight: " + state.getGoHomeHeight());
        textView.setText(stringBuffer.toString());


        System.out.println("--------state-----------");
//        System.out.println(("state is " + state));
        System.out.println(("getAircraftLocation is " + state.getAircraftLocation()));
        System.out.println(("getTakeoffLocationAltitude is " + state.getTakeoffLocationAltitude()));
        System.out.println("getAttitude: " + "{pitch:" + state.getAttitude().pitch + ", roll:" + state.getAttitude().roll + ", yaw:" + state.getAttitude().yaw + "}");
        System.out.println(("getVelocityX is " + state.getVelocityX()));
        System.out.println(("getVelocityY is " + state.getVelocityY()));
        System.out.println(("getVelocityZ is " + state.getVelocityZ()));
        System.out.println(("getFlightTimeInSeconds is " + state.getFlightTimeInSeconds()));
        System.out.println(("getFlightMode is " + state.getFlightMode()));
        System.out.println(("getFlightModeString is " + state.getFlightModeString()));
        System.out.println(("getSatelliteCount is " + state.getSatelliteCount()));
        System.out.println(("getGPSSignalLevel is " + state.getGPSSignalLevel()));
        System.out.println(("getUltrasonicHeightInMeters is " + state.getUltrasonicHeightInMeters()));
        System.out.println(("getOrientationMode is " + state.getOrientationMode()));
        System.out.println(("getBatteryThresholdBehavior is " + state.getBatteryThresholdBehavior()));
        System.out.println(("getFlightWindWarning is " + state.getFlightWindWarning()));
        System.out.println(("getFlightCount is " + state.getFlightCount()));
        System.out.println(("getFlightLogIndex is " + state.getFlightLogIndex()));
        System.out.println(("getHomeLocation is " + state.getHomeLocation()));
        System.out.println(("getGoHomeAssessment is " + state.getGoHomeAssessment()));
        System.out.println(("getGoHomeExecutionState is " + state.getGoHomeExecutionState()));
        System.out.println(("getGoHomeHeight is " + state.getGoHomeHeight()));
        System.out.println(("getGoHomeHeight is " + state.getGoHomeHeight()));


    }


    private void startTakeoff() {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        FlightController flightController = aircraft.getFlightController();
        flightController.startTakeoff(new Utility.Callback("startTakeoff"));
    }


    private void startLanding() {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        FlightController flightController = aircraft.getFlightController();
        flightController.startLanding(new Utility.Callback("startLanding"));
    }


    private void turnOffMotors() {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        FlightController flightController = aircraft.getFlightController();
        flightController.turnOffMotors(new Utility.Callback("turnOffMotors"));
    }

    private void turnOnMotors() {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        FlightController flightController = aircraft.getFlightController();
        flightController.turnOnMotors(new Utility.Callback("turnOnMotors"));
    }

    private void setVirtualStickModeEnabled() {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        FlightController flightController = aircraft.getFlightController();

        if (flightController.isVirtualStickControlModeAvailable()) return;
        flightController.setVirtualStickModeEnabled(true, new Utility.Callback("setVirtualStickModeEnabled"));
    }

    private void sendVirtualStickFlightControlData(float pitch, float roll, float yaw, float verticalThrottle) {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        FlightController flightController = aircraft.getFlightController();


        if (!flightController.isVirtualStickControlModeAvailable()) {
            System.out.println("isVirtualStickControlModeAvailable is false");
            return;
        }

        FlightControlData flightControlData = new FlightControlData(pitch, roll, yaw, verticalThrottle);
        flightController.sendVirtualStickFlightControlData(flightControlData, new Utility.Callback("sendVirtualStickFlightControlData"));
    }


}

