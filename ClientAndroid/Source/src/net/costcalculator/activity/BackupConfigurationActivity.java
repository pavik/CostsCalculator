/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;

/**
 * Backup configuration screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class BackupConfigurationActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_configuration);
        LOG.T("BackupConfigurationActivity::onCreate");
        logic_ = new BackupConfigurationLogic(this);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (logic_ != null)
        {
            logic_.release();
            logic_ = null;
        }

        LOG.T("BackupConfigurationActivity::onDestroy");
    }

    private BackupConfigurationLogic logic_;
}
