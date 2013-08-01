/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import net.costcalculator.activity.BackupService;
import net.costcalculator.util.LOG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receive alarm notification and perform backup copy of user data.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class BackupAlarmBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        LOG.T("BackupAlarmBroadcastReceiver::onReceive");

        try
        {
            BackupService.backupExpensesDropbox(context);
        }
        catch (Exception e)
        {
            LOG.E(e.getMessage());
            // TODO ErrorHandler.handleException(e, context);
        }
    }

}
