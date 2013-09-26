/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IncomeItemsLogic
{
    public IncomeItemsLogic()
    {
        LOG.T("IncomeItemsLogic::IncomeItemsLogic()");
    }

    public void detach()
    {
    }

    public void saveInstanceState(Bundle outState)
    {
    }

    public View attach(Fragment f, LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState)
    {
        fragment_ = f;
        View view = inflater.inflate(R.layout.expense_fragment, container,
                false);
        return view;
    }

    public void release()
    {
        LOG.T("IncomeItemsLogic::release()");
        fragment_ = null;
    }

    public void newCategoryRequest()
    {
    }

    private Fragment fragment_;
}
