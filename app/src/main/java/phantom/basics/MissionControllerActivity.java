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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.gimbal.CapabilityKey;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.Rotation;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.common.util.DJIParamCapability;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;

import static dji.common.error.DJIWaypointV2Error.UNKNOWN;
import static dji.common.gimbal.RotationMode.RELATIVE_ANGLE;
import static dji.common.mission.waypoint.WaypointMissionFinishedAction.NO_ACTION;
import static dji.common.mission.waypoint.WaypointMissionFlightPathMode.NORMAL;
import static dji.common.mission.waypoint.WaypointMissionHeadingMode.AUTO;
import static dji.common.mission.waypoint.WaypointMissionState.NOT_SUPPORTED;
import static dji.common.mission.waypoint.WaypointMissionState.READY_TO_EXECUTE;

/**
 * Copyright (C) 湖北无垠智探科技发展有限公司
 * Author: zuoz
 * Date: 2020/12/5 11:38
 * Description:
 * History:
 */
public class MissionControllerActivity extends AppCompatActivity {

    private TextView textView;
    private TextureView textureView;
    private BaseProduct baseProduct;
    private DJICodecManager mCodecManager;
    private String json = "[{\"id\": \"402881ee74d22f740174d3e77d4a00c2\", \"order\": 1, \"latitude\": 30.57702304651146, \"longitude\": 114.3255631585092 }, {\"id\": \"402881ee74d22f740174d3e77d4a00c3\", \"order\": 2, \"latitude\": 30.578875819096854, \"longitude\": 114.32554463016572 }, {\"id\": \"402881ee74d22f740174d3e77d4a00c4\", \"order\": 3, \"latitude\": 30.578948016673394, \"longitude\": 114.32494360878181 }, {\"id\": \"402881ee74d22f740174d3e77d4a00c5\", \"order\": 4, \"latitude\": 30.57715219691913, \"longitude\": 114.32496156757799 }, {\"id\": \"402881ee74d22f740174d3e77d4a00c6\", \"order\": 5, \"latitude\": 30.5772813473268, \"longitude\": 114.32435997664678 }, {\"id\": \"402881ee74d22f740174d3e77d4a00c7\", \"order\": 6, \"latitude\": 30.579020214249933, \"longitude\": 114.3243425873979 }, {\"id\": \"402881ee74d22f740174d3e77d4a00c8\", \"order\": 7, \"latitude\": 30.579092411826473, \"longitude\": 114.32374156601398 }, {\"id\": \"402881ee74d22f740174d3e77d4a00c9\", \"order\": 8, \"latitude\": 30.577410497734473, \"longitude\": 114.32375838571555 }, {\"id\": \"402881ee74d22f740174d3e77d4a00ca\", \"order\": 9, \"latitude\": 30.57753964814215, \"longitude\": 114.32315679478434 }, {\"id\": \"402881ee74d22f740174d3e77d4a00cb\", \"order\": 10, \"latitude\": 30.579164609403016, \"longitude\": 114.32314054463006 }, {\"id\": \"402881ee74d22f740174d3e77d4a00cc\", \"order\": 11, \"latitude\": 30.579236806979555, \"longitude\": 114.32253952324612 }, {\"id\": \"402881ee74d22f740174d3e77d4a00cd\", \"order\": 12, \"latitude\": 30.577668798549816, \"longitude\": 114.32255520385311 }, {\"id\": \"402881ee74d22f740174d3e77d4a00ce\", \"order\": 13, \"latitude\": 30.57779794895749, \"longitude\": 114.32195361292189 }, {\"id\": \"402881ee74d22f740174d3e77d4a00cf\", \"order\": 14, \"latitude\": 30.579309004556094, \"longitude\": 114.3219385018622 }, {\"id\": \"402881ee74d22f740174d3e77d4a00d0\", \"order\": 15, \"latitude\": 30.579381202132637, \"longitude\": 114.3213374804783 }, {\"id\": \"402881ee74d22f740174d3e77d4a00d1\", \"order\": 16, \"latitude\": 30.57792709936516, \"longitude\": 114.32135202199069 }, {\"id\": \"402881ee74d22f740174d3e77d4a00d2\", \"order\": 17, \"latitude\": 30.57805624977283, \"longitude\": 114.32075043105948 }, {\"id\": \"402881ee74d22f740174d3e77d4a00d3\", \"order\": 18, \"latitude\": 30.57925195973024, \"longitude\": 114.3207384735613 }, {\"id\": \"402881ee74d22f740174d3e77d4a00d4\", \"order\": 19, \"latitude\": 30.57853037505251, \"longitude\": 114.32014539026454 }, {\"id\": \"402881ee74d22f740174d3e77d4a00d5\", \"order\": 20, \"latitude\": 30.5781854001805, \"longitude\": 114.32014884012825 }]";


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

        setContentView(R.layout.activity_mission_controller);
        textView = findViewById(R.id.textView);
        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                log("~~SurfaceTextureListener.onSurfaceTextureAvailable~~");
                log("surface is " + surface);
                log("width is " + width);
                log("height is " + height);

//                startSDKRegistration();
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

        waypointMission();


    }


    public void pitch(View view) {
        System.out.println("~~button.pitch~~");

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        List<WayPointJson> wayPointJsons = gson.fromJson(json, List.class);

        System.out.println(wayPointJsons);


    }

    public void roll(View view) {
        System.out.println("~~button.roll~~");
        startSDKRegistration();

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
                                mCodecManager = new DJICodecManager(MissionControllerActivity.this, textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
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
                DJISDKManager.getInstance().registerApp(MissionControllerActivity.this.getApplicationContext(), new DJISDKManager.SDKManagerCallback() {
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
                            MissionControllerActivity.this.baseProduct = baseProduct;
//                            preview();

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


    private void waypointMission() {
        MissionControl missionControl = DJISDKManager.getInstance().getMissionControl();
        WaypointMissionOperator operator = missionControl.getWaypointMissionOperator();
        operator.addListener(new WaypointMissionOperatorListener() {
            @Override
            public void onDownloadUpdate(WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
                System.out.println("~~onDownloadUpdate~~");
                System.out.println("waypointMissionDownloadEvent is " + waypointMissionDownloadEvent);
                WaypointMissionState state = operator.getCurrentState();
                System.out.println(state);

            }

            @Override
            public void onUploadUpdate(WaypointMissionUploadEvent waypointMissionUploadEvent) {
                System.out.println("~~onUploadUpdate~~");
                System.out.println("waypointMissionUploadEvent is " + waypointMissionUploadEvent);

                if(waypointMissionUploadEvent.getCurrentState().equals(READY_TO_EXECUTE))
                operator.startMission(new Utility.Callback("startMission"));


            }

            @Override
            public void onExecutionUpdate(WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
                System.out.println("~~onExecutionUpdate~~");
                System.out.println("waypointMissionExecutionEvent is " + waypointMissionExecutionEvent);
                System.out.println("totalWaypointCount is " + waypointMissionExecutionEvent.getProgress().totalWaypointCount);

            }

            @Override
            public void onExecutionStart() {
                System.out.println("~~onExecutionStart~~");
            }

            @Override
            public void onExecutionFinish(DJIError djiError) {
                System.out.println("~~onExecutionFinish~~");
                System.out.println("djiError is " + djiError);

                operator.removeListener(this);

            }
        });

        WaypointMission.Builder builder = new WaypointMission.Builder()
                .finishedAction(NO_ACTION)
                .headingMode(AUTO)
                .autoFlightSpeed(15f)
                .maxFlightSpeed(15f)
                .flightPathMode(NORMAL);

//        .headingMode(WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER)
//                .gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY)







        float hight = 50f;
        Waypoint waypoint = null;
        WaypointAction waypointAction1 = new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, -90);
        WaypointAction waypointAction2 = new WaypointAction(WaypointActionType.GIMBAL_PITCH, -90);

        waypoint = new Waypoint(30.5769d, 114.3218d, hight);
        waypoint.addAction(waypointAction1);
        waypoint.addAction(waypointAction2);
        waypoint.shootPhotoTimeInterval = 300f;
        builder.addWaypoint(waypoint);


        waypoint = new Waypoint(30.57702304651146d, 114.3255631585092d, hight);
        waypoint.addAction(waypointAction1);
        waypoint.addAction(waypointAction2);
        builder.addWaypoint(waypoint);

        waypoint = new Waypoint(30.5769d, 114.3218d, hight);
        waypoint.addAction(waypointAction1);
        waypoint.addAction(waypointAction2);
        builder.addWaypoint(waypoint);




        System.out.println("size is " + builder.getWaypointList().size());
        System.out.println("getWaypointCount is " + builder.getWaypointCount());

        DJIError djiError = operator.loadMission(builder.build());
        if (djiError != null) {
            System.out.println("djiError is " + djiError.getDescription());
            return;
        }

        operator.uploadMission(new Utility.Callback("uploadMission"));






//        switch (state) {
//            case WaypointMissionState.UPLOADING:
//                System.out.println("");break;
//
//
//            default:
//                System.out.println("defalut!");
//        }


//        operator.startMission(new Utility.Callback("startMission"));
    }


}

