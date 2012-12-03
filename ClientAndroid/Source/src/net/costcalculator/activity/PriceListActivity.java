/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

/**
 * Price list screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class PriceListActivity extends Activity
{
    public static final String COST_ITEM_ID = "cost_item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LOG.T("PriceListActivity::onCreate");

        Intent intent = getIntent();
        try
        {
            setContentView(R.layout.activity_price_list);
            view_ = new PricelListLogic(this, intent.getLongExtra(COST_ITEM_ID,
                    0));
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, this);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LOG.T("PriceListActivity::onDestroy");

        if (view_ != null)
        {
            view_.release();
            view_ = null;
        }
    }

    private PricelListLogic view_;
}
