/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.activity.R;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Expense items screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ExpenseItemsActivity extends Activity implements
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

            ImageButton newButton = (ImageButton) findViewById(R.id.new_expense_item);
            newButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (logic_ != null)
                        logic_.newExpenseCategoryRequest();
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

        logic_.onActivityRestart();
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
            startActivity(new Intent(this, StatisticReportActivity.class));
            break;

        case R.id.backup_expenses:
            startActivity(new Intent(this, BackupActivity.class));
            break;
        }
    }

    private ExpenseItemsLogic logic_;
}
