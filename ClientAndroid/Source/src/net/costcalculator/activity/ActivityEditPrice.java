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
import net.costcalculator.util.LOG;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Activity is responsible for editing expense item from history.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ActivityEditPrice extends FragmentActivity
{
    public final static String EXTRA_COST_ITEM_RECORD_ID = "cir_id";
    public final static String EXTRA_COST_ITEM_ID        = "ci_id";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        LOG.T("ActivityEditPrice::onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_price);

        FragmentEditPrice fragment = null;
        final String FTAG = "fragment_edit_price";
        Fragment f = getSupportFragmentManager().findFragmentByTag(FTAG);
        if (f != null && f instanceof FragmentEditPrice)
            fragment = (FragmentEditPrice) f;

        LOG.D("ActivityEditPrice::onCreate - fragment reusing: "
                + (fragment != null));
        if (fragment == null)
        {
            fragment = new FragmentEditPrice();
            Intent intent = getIntent();
            long cirid = intent.getLongExtra(EXTRA_COST_ITEM_RECORD_ID, 0);
            long ciid = intent.getLongExtra(EXTRA_COST_ITEM_ID, 0);
            fragment.setCostItemRecordId(cirid);
            fragment.setCostItemId(ciid);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_edit_price, fragment, FTAG).commit();
        }

        fragment.setOkCallback(new OkCallback()
        {
            @Override
            public void ok(long r)
            {
                finishActivityWithResult(r);
            }
        });
        fragment.setCancelCallback(new CancelCallback()
        {
            @Override
            public void cancel()
            {
                finishActivity();
            }
        });
    }

    public void finishActivityWithResult(long result)
    {
        LOG.T("ActivityEditPrice::finishActivityWithResult");
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_COST_ITEM_RECORD_ID, result);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void finishActivity()
    {
        LOG.T("ActivityEditPrice::finishActivity");
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        LOG.T("ActivityEditPrice::onDestroy");
        super.onDestroy();
    }
}
