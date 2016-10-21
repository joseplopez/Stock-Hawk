package com.sam_chordas.android.stockhawk;

import android.content.Context;

/**
 * Created by jple on 12/10/16.
 *
 */

public class MyApp extends android.app.Application {

    private static MyApp instance;

    public MyApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}