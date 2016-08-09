/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;

/**
 * Backup screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class BackupActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        LOG.T("BackupActivity::onCreate");

        logic_ = new BackupLogic(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LOG.T("BackupActivity::onResume");

        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("BackupActivity::onResume");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);

        logic_.onActivityResume();
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

        LOG.T("BackupActivity::onDestroy");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("BackupActivity::onStart");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("BackupActivity::onStop");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    private BackupLogic logic_;
}
