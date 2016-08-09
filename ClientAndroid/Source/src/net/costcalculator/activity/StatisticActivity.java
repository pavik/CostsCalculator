
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemsService;
import net.costcalculator.util.LOG;
import android.content.res.Resources;
import android.os.Bundle;

public class StatisticActivity extends SliderActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        LOG.T("StatisticActivity::onCreate");
        super.onCreate(savedInstanceState);

        CostItemsService s = new CostItemsService(this);
        costitems_ = s.getAllCostItems();
        expensesdates_ = s.getDistinctExpensesDates(false);
        expensestagdates_ = s.getDistinctExpensesDates(true);
        tags_ = s.getAllDistinctTags();
        s.release();

        selCat_ = new ArrayList<Integer>();
        selTag_ = new ArrayList<Integer>();
        if (savedInstanceState != null)
        {
            int[] items1 = savedInstanceState.getIntArray("StatisticActivity1");
            if (items1 != null)
                for (int i = 0; i < items1.length; ++i)
                    selCat_.add(items1[i]);
            int[] items2 = savedInstanceState.getIntArray("StatisticActivity2");
            if (items2 != null)
                for (int i = 0; i < items2.length; ++i)
                    selTag_.add(items2[i]);
        }
        else
        {
            for (int i = 0; i < costitems_.size(); ++i)
                selCat_.add(i);
            for (int i = 0; i < tags_.size(); ++i)
                selTag_.add(i);
        }

        Resources r = getResources();
        titles_ = new String[] { r.getString(R.string.s_daily_expenses),
                r.getString(R.string.s_weekly_expenses),
                r.getString(R.string.s_monthly_expenses),
                r.getString(R.string.s_yearly_expenses),
                r.getString(R.string.s_whole_expenses),
                r.getString(R.string.s_period_expenses),
                r.getString(R.string.s_daily_expenses_tag),
                r.getString(R.string.s_weekly_expenses_tag),
                r.getString(R.string.s_monthly_expenses_tag),
                r.getString(R.string.s_yearly_expenses_tag),
                r.getString(R.string.s_whole_expenses_tag),
                r.getString(R.string.s_period_expenses_tag) };

        showfragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (outState != null && selCat_ != null)
        {
            int[] items = new int[selCat_.size()];
            for (int i = 0; i < selCat_.size(); ++i)
                items[i] = selCat_.get(i);
            outState.putIntArray("StatisticActivity1", items);
        }
        if (outState != null && selTag_ != null)
        {
            int[] items = new int[selTag_.size()];
            for (int i = 0; i < selTag_.size(); ++i)
                items[i] = selTag_.get(i);
            outState.putIntArray("StatisticActivity2", items);
        }
    }

    @Override
    protected void onDestroy()
    {
        LOG.T("StatisticActivity::onDestroy");
        super.onDestroy();
    }

    @Override
    protected SliderFragment getFragment(int index)
    {
        StatisticFragment f = SliderFragmentFactory.createStatisticFragment(
                this, fragments_[index], costitems_, expensesdates_,
                expensestagdates_, tags_, selCat_, selTag_);
        return f;
    }

    @Override
    protected String getHeaderTitle(int index)
    {
        if (index >= 0 && index < titles_.length)
            return titles_[index];
        else
            return "";
    }

    @Override
    protected String[] getFragmentTitles()
    {
        return titles_;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("StatisticActivity::onStart");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("StatisticActivity::onResume");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        AndroidApplication application = (AndroidApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("StatisticActivity::onStop");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.setScreenName(null);
    }

    private ArrayList<Date>     expensesdates_;
    private ArrayList<Date>     expensestagdates_;
    private ArrayList<CostItem> costitems_;
    private ArrayList<String>   tags_;
    private String[]            titles_;
    private ArrayList<Integer>  selCat_, selTag_;
}
