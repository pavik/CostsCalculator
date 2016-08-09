
package net.costcalculator.activity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import net.costcalculator.util.LOG;
import android.app.Application;

public class AndroidApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        LOG.INITIALIZE();
        LOG.D("Application started");
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        LOG.D("Application onLowMemory");
    }

    synchronized public Tracker getDefaultTracker()
    {
        if (mTracker == null)
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-43787753-1");
        }
        return mTracker;
    }

    private Tracker mTracker;
}