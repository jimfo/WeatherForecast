package com.jamesfody.weatherforecast.Objects;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jim on 12/9/2017.
 */

public class StaticContext extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        StaticContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return StaticContext.context;
    }
}
