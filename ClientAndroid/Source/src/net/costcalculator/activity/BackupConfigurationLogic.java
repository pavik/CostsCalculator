/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.service.BackupAlarmBroadcastReceiver;
import net.costcalculator.service.PreferencesService;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Logic is responsible for saving backup copy options in the system.
 * 
 * <pre>
 * Usage:
 * {
 *     // create instance
 *     BackupConfigurationLogic l = new BackupConfigurationLogic(activity);
 * 
 *     // activity uses logic
 * 
 *     // destroy view
 *     l.release();
 *     l = null;
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class BackupConfigurationLogic
{
    public BackupConfigurationLogic(Activity a)
    {
        activity_ = a;
        pref = new PreferencesService(activity_);

        etHour_ = (EditText) activity_.findViewById(R.id.et_hours);
        String hour = pref.get(PreferencesService.BACKUP_INTERVAL);
        etHour_.setText(hour == null ? "0" : hour);

        btnSave_ = (Button) activity_.findViewById(R.id.btn_save);
        btnSave_.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                saveConfiguration();
            }
        });
    }

    public void release()
    {
        pref = null;
    }

    private void saveConfiguration()
    {
        String shour = etHour_.getText().toString();
        pref.set(PreferencesService.BACKUP_INTERVAL, shour);

        int hour = 0;
        if (shour.length() > 0)
            hour = Integer.parseInt(shour);
        if (hour > 0)
            setupAlarm(hour);
        else
            deleteAlarm();
        activity_.finish();
    }

    private void setupAlarm(int hours)
    {
        AlarmManager am = (AlarmManager) activity_
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity_,
                BackupAlarmBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(activity_, 0,
                intent, 0);
        int repeatingms = hours * 3600 * 1000;
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis()
                + repeatingms, repeatingms, pending);
    }

    private void deleteAlarm()
    {
        AlarmManager am = (AlarmManager) activity_
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity_,
                BackupAlarmBroadcastReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(activity_, 0,
                intent, 0);
        am.cancel(pending);
    }

    private Activity           activity_;
    private EditText           etHour_;
    private Button             btnSave_;
    private PreferencesService pref;
}
