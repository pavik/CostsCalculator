
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemsService;
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
        return fragments_[index]; // TODO
    }

    private ArrayList<Date>     expensesdates_;
    private ArrayList<Date>     expensestagdates_;
    private ArrayList<CostItem> costitems_;
    private ArrayList<String>   tags_;
}
