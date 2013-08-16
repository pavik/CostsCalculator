
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemsService;
import android.content.res.Resources;
import android.os.Bundle;

public class StatisticActivity extends SliderActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        CostItemsService s = new CostItemsService(this);
        costitems_ = s.getAllCostItems();
        expensesdates_ = s.getDistinctExpensesDates(false);
        expensestagdates_ = s.getDistinctExpensesDates(true);
        tags_ = s.getAllDistinctTags();
        s.release();

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
    protected SliderFragment getFragment(int index)
    {
        StatisticFragment f = SliderFragmentFactory.createStatisticFragment(
                this, fragments_[index], costitems_, expensesdates_,
                expensestagdates_, tags_);
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

    private ArrayList<Date>     expensesdates_;
    private ArrayList<Date>     expensestagdates_;
    private ArrayList<CostItem> costitems_;
    private ArrayList<String>   tags_;
    private String[]            titles_;
}
