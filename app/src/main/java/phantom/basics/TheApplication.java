package phantom.basics;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.secneo.sdk.Helper;

/**
 * Copyright (C) 湖北无垠智探科技发展有限公司
 * Author: zuoz
 * Date: 2020/10/29 14:35
 * Description:
 * History:
 */
public class TheApplication extends Application {

    private FPVApplication fpvApplication;

    public TheApplication() {
        super();
        System.out.println("------ " + getClass().getSimpleName() + ".Constructor  ------");
    }

    @Override
    public void onCreate() {
        System.out.println("*********  " + getClass().getSimpleName() + ".onCreate  *********");
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        System.out.println("*********  " + getClass().getSimpleName() + ".onTerminate  *********");
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        System.out.println("*********  " + getClass().getSimpleName() + ".onConfigurationChanged  *********");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        System.out.println("*********  " + getClass().getSimpleName() + ".onLowMemory  *********");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        System.out.println("*********  " + getClass().getSimpleName() + ".onTrimMemory  *********");
        super.onTrimMemory(level);
    }

    @Override
    protected void attachBaseContext(Context base) {
        System.out.println("*********  " + getClass().getSimpleName() + ".attachBaseContext  *********");
        super.attachBaseContext(base);
        System.out.println("initilize start");
        Helper.install(this);
        System.out.println("initilize end");
    }
}
