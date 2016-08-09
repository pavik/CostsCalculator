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

import net.costcalculator.activity.R;
import net.costcalculator.dialog.EditTextDialog;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Expense items screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ExpenseItemsActivity extends FragmentActivity implements
        View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // initialize global resources
        LOG.INITIALIZE();
        LOG.T("ExpenseItemsActivity::onCreate");

        setContentView(R.layout.activity_expense_items);
        try
        {
            logic_ = new ExpenseItemsLogic(this);
            final EditTextDialog etd = logic_.createEditTextDialog(true);

            ImageButton newButton = (ImageButton) findViewById(R.id.new_expense_item);
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    etd.show(getSupportFragmentManager(),
                            ExpenseItemsLogic.TAG_EDIT_DLG);
                }
            });

            ImageButton reportButton = (ImageButton) findViewById(R.id.expense_statistic);
            reportButton.setOnClickListener(this);

            ImageButton backupButton = (ImageButton) findViewById(R.id.backup_expenses);
            backupButton.setOnClickListener(this);

            ImageButton quitButton = (ImageButton) findViewById(R.id.quit_application);
            quitButton.setOnClickListener(this);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, this);
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        LOG.T("ExpenseItemsActivity::onRestart");

        logic_.refreshView();
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

        LOG.T("ExpenseItemsActivity::onDestroy");
        LOG.RELEASE();
    }

    @Override
    public void onClick(View v)
    {
        LOG.T("ExpenseItemsActivity::onClick");

        if (v.getId() == R.id.quit_application)
            finish();
        else if (v.getId() == R.id.expense_statistic)
        {
            Intent intent = new Intent(this, StatisticActivity.class);
            String[] fragments = {
                    SliderFragmentFactory
                            .getCatPath(SliderFragmentFactory.PATH_STAT_DAILY),
                    SliderFragmentFactory
                            .getCatPath(SliderFragmentFactory.PATH_STAT_WEEKLY),
                    SliderFragmentFactory
                            .getCatPath(SliderFragmentFactory.PATH_STAT_MONTHLY),
                    SliderFragmentFactory
                            .getCatPath(SliderFragmentFactory.PATH_STAT_YEARLY),
                    SliderFragmentFactory
                            .getCatPath(SliderFragmentFactory.PATH_STAT_FOREVER),
                    SliderFragmentFactory
                            .getCatPath(SliderFragmentFactory.PATH_STAT_CUSTOM),
                    SliderFragmentFactory
                            .getTagPath(SliderFragmentFactory.PATH_STAT_DAILY),
                    SliderFragmentFactory
                            .getTagPath(SliderFragmentFactory.PATH_STAT_WEEKLY),
                    SliderFragmentFactory
                            .getTagPath(SliderFragmentFactory.PATH_STAT_MONTHLY),
                    SliderFragmentFactory
                            .getTagPath(SliderFragmentFactory.PATH_STAT_YEARLY),
                    SliderFragmentFactory
                            .getTagPath(SliderFragmentFactory.PATH_STAT_FOREVER),
                    SliderFragmentFactory
                            .getTagPath(SliderFragmentFactory.PATH_STAT_CUSTOM) };
            intent.putExtra(SliderActivity.EXTRA_FRAGMENTS, fragments);
            startActivity(intent);
        }
        else if (v.getId() == R.id.backup_expenses)
            startActivity(new Intent(this, BackupActivity.class));
    }

    @Override
    public void onStart()
    {
        super.onStart();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("ExpenseItemsActivity::onStart");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("ExpenseItemsActivity::onResume");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("ExpenseItemsActivity::onStop");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }
    private ExpenseItemsLogic logic_;
}
