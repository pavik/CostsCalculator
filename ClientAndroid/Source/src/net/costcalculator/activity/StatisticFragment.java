
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import net.costcalculator.activity.AdvancedStatisticAdapter.StatisticPeriod;
import net.costcalculator.service.CostItem;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class StatisticFragment extends SliderFragment
{
    public StatisticFragment()
    {
        cat_ = true;
        layoutid_ = R.layout.view_statistic_report;
        adapter_ = new AdvancedStatisticAdapter();
        items_ = new String[0];
        selectedItems_ = new ArrayList<Integer>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
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
            {
                items_[i] = costitems_.get(i).getName();
                selectedItems_.add(i);
            }
        }
        else
        {
            items_ = new String[tags_.size()];
            for (int i = 0; i < tags_.size(); ++i)
            {
                items_[i] = tags_.get(i);
                selectedItems_.add(i);
            }
            adapter_.setTagFilter(tags_);
        }

        lv.setAdapter(adapter_);
        if (pagelistener_ != null)
            lv.setOnTouchListener(pagelistener_);
        return view;
    }

    public void initialize(Context c, boolean cat)
    {
        cat_ = cat;
        adapter_ = new AdvancedStatisticAdapter(c, null, null, period_, -1,
                costitems_, expensesdates_,
                cat ? AdvancedStatisticAdapter.REPORT_CAT
                        : AdvancedStatisticAdapter.REPORT_TAG);
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

    private void showSelectionDialog()
    {
        boolean[] selected = new boolean[items_.length];
        for (int i = 0; i < selected.length; ++i)
            selected[i] = selectedItems_.contains(i);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.label_select_category)
                .setMultiChoiceItems(items_, selected,
                        new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which, boolean isChecked)
                            {
                                if (isChecked)
                                {
                                    selectedItems_.add(which);
                                }
                                else if (selectedItems_.contains(which))
                                {
                                    selectedItems_.remove(Integer
                                            .valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                if (cat_)
                                {
                                    ArrayList<CostItem> costitems = new ArrayList<CostItem>();
                                    for (int i = 0; i < selectedItems_.size(); ++i)
                                        costitems.add(costitems_
                                                .get(selectedItems_.get(i)));
                                    adapter_.setFilter(costitems);
                                }
                                else
                                {
                                    ArrayList<String> tags = new ArrayList<String>();
                                    for (int i = 0; i < selectedItems_.size(); ++i)
                                        tags.add(tags_.get(selectedItems_
                                                .get(i)));
                                    adapter_.setTagFilter(tags);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                            }
                        });

        AlertDialog d = builder.create();
        d.setCanceledOnTouchOutside(true);
        d.show();

        Button bok = d.getButton(AlertDialog.BUTTON_POSITIVE);
        bok.setBackgroundResource(R.drawable.green_gradient);
        bok.setTextAppearance(d.getContext(), R.style.GradientButtonText);
        Button bcancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
        bcancel.setBackgroundResource(R.drawable.green_gradient);
        bcancel.setTextAppearance(d.getContext(), R.style.GradientButtonText);
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
}