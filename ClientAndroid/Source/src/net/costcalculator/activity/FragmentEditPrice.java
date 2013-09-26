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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Part of screen: price, currency, date, comment, tag.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class FragmentEditPrice extends Fragment
{
    public FragmentEditPrice()
    {
        cirId_ = 0;
        ciId_ = 0;
        ok_ = null;
        cancel_ = null;
        logic_ = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        LOG.T("FragmentEditPrice::onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy()
    {
        LOG.T("FragmentEditPrice::onDestroy");
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        LOG.T("FragmentEditPrice::onCreateView");
        restoreInstanceState(savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_edit_price, container,
                false);
        logic_ = new LogicEditPrice(this, v, cirId_, ciId_, savedInstanceState);
        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        LOG.T("FragmentEditPrice::onDestroyView");
        if (logic_ != null)
        {
            logic_.release();
            logic_ = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        LOG.T("FragmentEditPrice::onSaveInstanceState");
        if (outState != null)
        {
            outState.putLong("f1", ciId_);
            outState.putLong("f2", cirId_);
            if (logic_ != null)
                logic_.saveInstanceState(outState);
        }
    }

    public void restoreInstanceState(Bundle inState)
    {
        if (inState != null)
        {
            ciId_ = inState.getLong("f1");
            cirId_ = inState.getLong("f2");
        }
    }

    public void setCostItemRecordId(long id)
    {
        cirId_ = id;
    }

    public void setCostItemId(long id)
    {
        ciId_ = id;
    }

    public void setOkCallback(OkCallback c)
    {
        ok_ = c;
    }

    public void setCancelCallback(CancelCallback c)
    {
        cancel_ = c;
    }

    public void okRequest(long r)
    {
        if (ok_ != null)
            ok_.ok(r);
    }

    public void cancelRequest()
    {
        if (cancel_ != null)
            cancel_.cancel();
    }

    public void hideFragment()
    {
        if (logic_ != null)
            logic_.hideViews();
    }

    public void showFragment()
    {
        if (logic_ != null)
            logic_.showViews();
    }

    private long           cirId_;
    private long           ciId_;
    private OkCallback     ok_;
    private CancelCallback cancel_;
    private LogicEditPrice logic_;
}
