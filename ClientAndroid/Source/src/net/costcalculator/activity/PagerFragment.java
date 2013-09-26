/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class PagerFragment extends Fragment
{
    public PagerFragment()
    {
        title_ = "placeholder";
    }

    public String getPageTitle()
    {
        return title_;
    }

    public void setPageTitle(String title)
    {
        title_ = title;
    }

    public int getPageIconId()
    {
        return -1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            title_ = savedInstanceState.getString("PagerFragment_1");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        if (outState != null)
            outState.putString("PagerFragment_1", title_);
    }

    private String title_;
}
