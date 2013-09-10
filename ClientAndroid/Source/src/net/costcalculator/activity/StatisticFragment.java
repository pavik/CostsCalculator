
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import net.costcalculator.activity.AdvancedStatisticAdapter.StatisticPeriod;
import net.costcalculator.dialog.MultiSelectionConfirmListener;
import net.costcalculator.dialog.MultiSelectionDialog;
import net.costcalculator.service.CostItem;
import net.costcalculator.util.LOG;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class StatisticFragment extends SliderFragment implements
        MultiSelectionConfirmListener
{
    public StatisticFragment()
    {
        cat_ = true;
        layoutid_ = R.layout.view_statistic_report;
        adapter_ = new AdvancedStatisticAdapter();
        tags_ = new ArrayList<String>();
        costitems_ = new ArrayList<CostItem>();
        expensesdates_ = new ArrayList<Date>();
        period_ = StatisticPeriod.DAY;
        items_ = new String[0];
        selectedItems_ = new ArrayList<Integer>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        LOG.T("StatisticFragment::onCreateView");

        View view = inflater.inflate(layoutid_, container, false);
        ListView lv = (ListView) view.findViewById(R.id.lv_stat_report);
        Button filter = (Button) view.findViewById(R.id.btn_select_items);
        filter.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showSelectionDialog();
            }
        });

        if (cat_)
        {
            items_ = new String[costitems_.size()];
            for (int i = 0; i < costitems_.size(); ++i)
                items_[i] = costitems_.get(i).getName();
        }
        else
        {
            items_ = new String[tags_.size()];
            for (int i = 0; i < tags_.size(); ++i)
                items_[i] = tags_.get(i);
        }

        lv.setAdapter(adapter_);
        if (pagelistener_ != null)
            lv.setOnTouchListener(pagelistener_);

        rebindListenersForActiveFragments();
        return view;
    }

    @Override
    public void onDestroy()
    {
        LOG.T("StatisticFragment::onDestroy");
        super.onDestroy();
    }

    public void initialize(Context c, boolean cat)
    {
        cat_ = cat;
        adapter_ = new AdvancedStatisticAdapter(c, null, null, period_, -1,
                costitems_, expensesdates_,
                cat ? AdvancedStatisticAdapter.REPORT_CAT
                        : AdvancedStatisticAdapter.REPORT_TAG);
        applyFilter();
    }

    public void setCostItems(ArrayList<CostItem> costitems)
    {
        costitems_ = costitems;
    }

    public void setTags(ArrayList<String> tags)
    {
        tags_ = tags;
    }

    public void setExpensesDates(ArrayList<Date> expensesdates)
    {
        expensesdates_ = expensesdates;
    }

    public void setPeriod(StatisticPeriod p)
    {
        period_ = p;
    }

    public void setSelectedItems(ArrayList<Integer> items)
    {
        selectedItems_ = items;
    }

    private void showSelectionDialog()
    {
        MultiSelectionDialog d = new MultiSelectionDialog();
        d.setHeaderId(R.string.label_select_category);
        d.setItems(items_);
        d.setSelectedItems(selectedItems_);
        d.setConfirmListener(this);
        d.show(getFragmentManager(), TAG_MULTISEL_DLG);
    }

    protected void applyFilter()
    {
        if (cat_)
        {
            ArrayList<CostItem> costitems = new ArrayList<CostItem>();
            for (int i = 0; i < selectedItems_.size(); ++i)
                costitems.add(costitems_.get(selectedItems_.get(i)));
            adapter_.setFilter(costitems);
        }
        else
        {
            ArrayList<String> tags = new ArrayList<String>();
            for (int i = 0; i < selectedItems_.size(); ++i)
                tags.add(tags_.get(selectedItems_.get(i)));
            adapter_.setTagFilter(tags);
        }
    }

    @Override
    public void onMultiSelectionConfirmed(int dialogid,
            ArrayList<Integer> selectedItems, int param)
    {
        selectedItems_.clear();
        selectedItems_.addAll(selectedItems);
        applyFilter();
    }

    private void rebindListenersForActiveFragments()
    {
        Fragment f = null;

        f = getFragmentManager().findFragmentByTag(TAG_MULTISEL_DLG);
        if (f != null && f instanceof MultiSelectionDialog)
        {
            MultiSelectionDialog md = (MultiSelectionDialog) f;
            md.setConfirmListener(this);
        }
    }

    protected boolean                  cat_;
    protected int                      layoutid_;
    protected AdvancedStatisticAdapter adapter_;
    protected ArrayList<String>        tags_;
    protected ArrayList<CostItem>      costitems_;
    protected ArrayList<Date>          expensesdates_;
    protected StatisticPeriod          period_;
    private String[]                   items_;
    private ArrayList<Integer>         selectedItems_;
    private static final String        TAG_MULTISEL_DLG = "fragment_multi_selection";
}