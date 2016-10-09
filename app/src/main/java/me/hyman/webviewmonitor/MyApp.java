package me.hyman.webviewmonitor;

import android.app.Application;

/**
 * Created by chengbin on 2016/10/9.
 */
public class MyApp extends Application {

    private static MyApp instance;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static MyApp getInstance() {
        return instance;
    }
}
