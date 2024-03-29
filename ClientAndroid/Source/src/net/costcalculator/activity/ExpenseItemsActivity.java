/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import com.google.analytics.tracking.android.EasyTracker;

import net.costcalculator.activity.R;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

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
            logic_ = new ExpenseItemsLogic();
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

        switch (v.getId())
        {
        case R.id.quit_application:
            finish();
            break;

        case R.id.expense_statistic:
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
            break;

        case R.id.backup_expenses:
            startActivity(new Intent(this, BackupActivity.class));
            break;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    private ExpenseItemsLogic logic_;
}
