/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.service.CancelCallback;
import net.costcalculator.service.OkCallback;
import net.costcalculator.util.ErrorHandler;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;

/**
 * Price list screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class PriceListActivity extends FragmentActivity
{
    public static final String COST_ITEM_ID = "cost_item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LOG.T("PriceListActivity::onCreate");

        try
        {
            setContentView(R.layout.activity_price_list);
            final long ciId = getIntent().getLongExtra(COST_ITEM_ID, 0);

            final String FTAG = "fragment_edit_price";
            Fragment f = getSupportFragmentManager().findFragmentByTag(FTAG);
            if (f != null && f instanceof FragmentEditPrice)
                fragment = (FragmentEditPrice) f;

            if (fragment == null)
            {
                fragment = new FragmentEditPrice();
                fragment.setCostItemId(ciId);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_edit_price, fragment, FTAG)
                        .commit();
            }

            fragment.setOkCallback(new OkCallback()
            {
                @Override
                public void ok(long r)
                {
                    okRequest(r);
                    fragment.hideFragment();
                }
            });
            fragment.setCancelCallback(new CancelCallback()
            {
                @Override
                public void cancel()
                {
                    cancelRequest();
                    fragment.hideFragment();
                }
            });

            logic_ = new PricelListLogic(this, ciId);
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

        if (logic_ != null)
        {
            logic_.release();
            logic_ = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (logic_ != null)
            logic_.onActivityResult(requestCode, resultCode, data);
    }

    private void okRequest(long r)
    {
        if (logic_ != null)
            logic_.okRequest(r);
    }

    private void cancelRequest()
    {
        if (logic_ != null)
            logic_.cancelRequest();
    }

    private PricelListLogic   logic_;
    private FragmentEditPrice fragment;
}
