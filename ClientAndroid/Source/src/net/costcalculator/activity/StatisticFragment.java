
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import net.costcalculator.activity.AdvancedStatisticAdapter.StatisticPeriod;
import net.costcalculator.service.CostItem;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StatisticFragment extends SliderFragment
{
    public StatisticFragment()
    {
        layoutid_ = R.layout.view_statistic_report;
        adapter_ = new AdvancedStatisticAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(layoutid_, container, false);
        ListView lv = (ListView) view.findViewById(R.id.lv_stat_report);
        lv.setAdapter(adapter_);
        if (pagelistener_ != null)
            lv.setOnTouchListener(pagelistener_);
        return view;
    }

    public void initialize(Context c)
    {
        adapter_ = new AdvancedStatisticAdapter(c, null, null, period_, -1,
                costitems_, expensesdates_);
    }

    public void setCostItems(ArrayList<CostItem> costitems)
    {
        costitems_ = costitems;
    }

    public void setExpensesDates(ArrayList<Date> expensesdates)
    {
        expensesdates_ = expensesdates;
    }

    public void setPeriod(StatisticPeriod p)
    {
        period_ = p;
    }

    protected int                      layoutid_;
    protected AdvancedStatisticAdapter adapter_;
    protected ArrayList<CostItem>      costitems_;
    protected ArrayList<Date>          expensesdates_;
    protected StatisticPeriod          period_;
}