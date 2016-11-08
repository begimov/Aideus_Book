package com.aideus.book;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;

public class AideusBookApplication extends Application {

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            enableStrictMode();
        }
        super.onCreate();
    }

    private void enableStrictMode() {
        if (Build.VERSION.SDK_INT >= 9) {
            doEnableStrictMode();
        }

        if (Build.VERSION.SDK_INT >= 16) {
            //restore strict mode after onCreate() returns.
            new Handler().postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    doEnableStrictMode();
                }
            });
        }
    }

    private void doEnableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
//                .penaltyDeath()
                .build());
    }

    public static AideusBookApplication get(Context context) {
        return (AideusBookApplication) context.getApplicationContext();
    }

}
