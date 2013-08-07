
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
        adapter_ = new AdvancedStatisticAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_statistic_report, container,
                false);
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

    private AdvancedStatisticAdapter adapter_;
    private ArrayList<CostItem>      costitems_;
    private ArrayList<Date>          expensesdates_;
    private StatisticPeriod          period_;
}