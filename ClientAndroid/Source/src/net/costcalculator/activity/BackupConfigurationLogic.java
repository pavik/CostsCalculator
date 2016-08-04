/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.service.PreferencesService;
import net.costcalculator.service.DropBoxService;
import android.app.Activity;
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
        String hourStr = etHour_.getText().toString();
        int hour = hourStr.length() == 0 ? 0 : Integer.parseInt(hourStr);

        if (hour <= 0)
        {
            pref.set(PreferencesService.BACKUP_INTERVAL, "0");
            DropBoxService.deleteAlarm(activity_);
        }
        else
        {
            pref.set(PreferencesService.BACKUP_INTERVAL, hourStr);
            DropBoxService.deleteAlarm(activity_);
            DropBoxService.setupAlarm(activity_);
        }

        activity_.finish();
    }

    private Activity           activity_;
    private EditText           etHour_;
    private Button             btnSave_;
    private PreferencesService pref;
}
