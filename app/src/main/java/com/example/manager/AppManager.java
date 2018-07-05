package com.example.manager;

import com.example.App;

/**
 * @author Is-Poson
 * @time 2018/7/5  23:23
 * @desc ${TODO}
 */

public class AppManager {

    private static AppManager appManager;
    private static App app;

    private AppManager() {
    }

    public static synchronized AppManager getInstance() {
        if (appManager == null) {
            synchronized (AppManager.class) {
                if (appManager == null) {
                    appManager = new AppManager();
                }
            }
        }
        return appManager;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public App getApp() {
        if (app == null)
            throw new RuntimeException("app 未存入");
        return app;
    }

}
