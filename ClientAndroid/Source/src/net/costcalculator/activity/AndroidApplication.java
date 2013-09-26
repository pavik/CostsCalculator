
package net.costcalculator.activity;

import net.costcalculator.service.DataCacheService;
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
        DataCacheService.destroyInstance();
    }
}
