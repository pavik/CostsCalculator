/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.fragment.FragmentManager;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IncomeFragment extends PagerFragment implements
        MainActionBarListener
{
    public static final String TAG = "IncomeFragment";

    public IncomeFragment()
    {
        LOG.T("IncomeFragment::IncomeFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        LOG.T("IncomeFragment::onCreate");
        super.onCreate(savedInstanceState);
        FragmentManager.instance().put(TAG, this);
        logic_ = new IncomeItemsLogic();
    }

    @Override
    public void onDestroy()
    {
        LOG.T("IncomeFragment::onDestroy");
        super.onDestroy();
        FragmentManager.instance().remove(TAG);
        logic_.release();
        logic_ = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        LOG.T("IncomeFragment::onCreateView");
        return logic_.attach(this, inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView()
    {
        LOG.T("IncomeFragment::onDestroyView");
        super.onDestroyView();
        logic_.detach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        LOG.T("IncomeFragment::onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (outState != null)
            logic_.saveInstanceState(outState);
    }

    @Override
    public void onNewItemClicked()
    {
        logic_.newCategoryRequest();
    }

    public int getPageIconId()
    {
        return R.drawable.ic_income;
    }

    IncomeItemsLogic logic_;
}
