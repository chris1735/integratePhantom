package phantom.basics;

import android.os.Handler;
import android.os.Looper;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;

/**
 * Copyright (C) 湖北无垠智探科技发展有限公司
 * Author: zuoz
 * Date: 2020/10/30 17:28
 * Description:
 * History:
 */
public class Utility {
    public static class Callback implements CommonCallbacks.CompletionCallback {
        String target;

        public Callback(String target) {
            this.target = target;
        }

        @Override
        public void onResult(DJIError djiError) {
            System.out.println("~~" + target + ".onResult~~");
            if (djiError == null) {
                System.out.println(target + " Succeeded");
            } else {
                System.out.println(djiError.getDescription());
            }
        }
    }







}
