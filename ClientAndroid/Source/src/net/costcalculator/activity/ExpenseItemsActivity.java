/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.activity.R;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * Expense items screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ExpenseItemsActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LOG.T("ExpenseItemsActivity::onCreate");

        setContentView(R.layout.activity_expense_items);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        LOG.T("ExpenseItemsActivity::onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_expense_items, menu);
        return true;
    }

}
