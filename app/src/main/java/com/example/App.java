package com.example;

import android.app.Application;

import com.example.manager.AppManager;

/**
 * @author Is-Poson
 * @time 2018/7/5  23:21
 * @desc ${TODO}
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppManager.getInstance().setApp(this);
    }
}
