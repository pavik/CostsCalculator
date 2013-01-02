/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.activity.R;
import net.costcalculator.service.CostItemsService;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
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
        CostItemsService.createInstance(getApplicationContext());

        setContentView(R.layout.activity_expense_items);
        try
        {
            logic_ = new ExpenseItemsLogic(this);

            ImageButton newButton = (ImageButton) findViewById(R.id.new_expense_item);
            newButton.setOnClickListener(logic_);

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

        CostItemsService.releaseInstance();
        LOG.T("ExpenseItemsActivity::onDestroy");
        LOG.RELEASE();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_expense_items, menu);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.quit_application:
            finish();
            break;
        }
    }

    private ExpenseItemsLogic logic_;
}
