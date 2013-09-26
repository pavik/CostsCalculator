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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements TabListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LOG.T("MainActivity::onCreate");

        setContentView(R.layout.activity_main);

        pager_ = (ViewPager) findViewById(R.id.pager);
        pages_ = new MainActivityPagerAdapter(getSupportFragmentManager(), this);
        pager_.setAdapter(pages_);
        pager_.setOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int index)
            {
                getSupportActionBar().setSelectedNavigationItem(index);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.action_bar_background));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (int i = 0; i < pages_.getCount(); ++i)
        {
            Tab tab = actionBar.newTab().setText(pages_.getPageTitle(i))
                    .setTabListener(this);
            if (pages_.getPageIconId(i) > 0)
                tab.setIcon(pages_.getPageIconId(i));
            actionBar.addTab(tab);
        }
    }

    @Override
    protected void onRestart()
    {
        LOG.T("MainActivity::onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        LOG.T("MainActivity::onDestroy");
        super.onDestroy();
        pager_ = null;
        pages_ = null;
        LOG.RELEASE();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
        case R.id.action_new:
            Fragment f = pages_.getItem(pager_.getCurrentItem());
            if (f instanceof MainActionBarListener)
                ((MainActionBarListener) f).onNewItemClicked();
            return true;
        case R.id.action_statistic:
            return true;
        case R.id.action_export:
            return true;
        case R.id.action_quit:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1)
    {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft)
    {
        pager_.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1)
    {
    }

    private ViewPager                pager_;
    private MainActivityPagerAdapter pages_;
}

final class MainActivityPagerAdapter extends FragmentPagerAdapter
{
    public MainActivityPagerAdapter(FragmentManager fm, ActionBarActivity a)
    {
        super(fm);
        a_ = a;
        pages_ = new PagerFragment[] { createFragment(0), createFragment(1) };
    }

    @Override
    public Fragment getItem(int i)
    {
        return pages_[i];
    }

    @Override
    public int getCount()
    {
        return pages_.length;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return pages_[position].getPageTitle();
    }

    public int getPageIconId(int position)
    {
        return pages_[position].getPageIconId();
    }

    private PagerFragment createFragment(int index)
    {
        switch (index)
        {
        case 0:
            ExpenseFragment page1 = net.costcalculator.fragment.FragmentManager
                    .instance()
                    .find(ExpenseFragment.TAG, ExpenseFragment.class);
            if (page1 == null)
                page1 = new ExpenseFragment();
            page1.setPageTitle(a_.getResources().getString(R.string.app_name));
            return page1;
        case 1:
            IncomeFragment page2 = net.costcalculator.fragment.FragmentManager
                    .instance().find(IncomeFragment.TAG, IncomeFragment.class);
            if (page2 == null)
                page2 = new IncomeFragment();
            page2.setPageTitle(a_.getResources().getString(R.string.income));
            return page2;
        }
        return null;
    }

    private ActionBarActivity a_;
    private PagerFragment[]   pages_;
}
