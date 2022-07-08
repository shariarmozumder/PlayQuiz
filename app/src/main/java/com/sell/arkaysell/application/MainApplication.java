package com.sell.arkaysell.application;

import android.app.Application;
import android.graphics.Typeface;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.sell.arkaysell.R;
import com.sell.arkaysell.utils.Prefs;


/**
 * Created by Ishan4452 on 5/12/2015.
 */

public class MainApplication extends Application
{
    public static final String TAG = MainApplication.class
            .getSimpleName();


    private RequestQueue mRequestQueue;
    private static MainApplication mInstance;

    private Tracker mTracker;
    private Typeface AugustSansRegular,AugustSansMedium,AugustSansBold;
    private Prefs prefs;


    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
            if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }


    public static synchronized MainApplication getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

         getInstance();
        mInstance = this;
        AugustSansRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "AugustSans-55Regular.ttf");
        AugustSansMedium = Typeface.createFromAsset(getApplicationContext().getAssets(), "AugustSans-65Medium.ttf");
        AugustSansBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "AugustSans-75Bold.ttf");
        prefs = new Prefs(this);

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public Prefs getPrefs() {
        return prefs;
    }


    public Typeface getAugustSansRegular() {
        return AugustSansRegular;
    }

    public Typeface getAugustSansMedium() {
        return AugustSansMedium;
    }


}
