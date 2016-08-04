
package net.costcalculator.service;

import net.costcalculator.util.LOG;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        LOG.T("BootCompletedBroadcastReceiver::onReceive");

        PreferencesService pref = new PreferencesService(context);
        if (pref.get(PreferencesService.ACCESS_TOKEN) != null)
            DropBoxService.setupAlarm(context);
        else
            LOG.D("Access token is not available.");
    }
}
