
package net.costcalculator.adapter;

import java.io.IOException;
import java.util.Date;

import net.costcalculator.activity.R;
import net.costcalculator.service.CostItemRecordsAdapter;
import net.costcalculator.util.ErrorHandler;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatisticDetailsAdapter extends BaseAdapter
{
    public StatisticDetailsAdapter(Activity a, Date from, Date to,
            String[] catguids, boolean catortag)
    {
        if (a == null || from == null || to == null || catguids == null)
            throw new IllegalArgumentException("invalid input");
        a_ = a;
        from_ = from;
        to_ = to;
        catguids_ = catguids;
        catortag_ = catortag;
    }

    public void setFilter(String[] values)
    {
        catguids_ = values;
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return catguids_.length;
    }

    @Override
    public Object getItem(int arg0)
    {
        return catguids_[arg0];
    }

    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        View view = a_.getLayoutInflater().inflate(
                R.layout.view_statistic_details, null);
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        CostItemRecordsAdapter adapter = new CostItemRecordsAdapter(a_,
                catguids_[pos], from_, to_, catortag_);
        if (catortag_)
            title.setText(adapter.getCostItemName());
        else
            title.setText(catguids_[pos]);

        RelativeLayout rl = (RelativeLayout) view;
        int below = title.getId();
        for (int i = 0; i < adapter.getCount(); ++i)
        {
            View item = adapter.getView(i, null, parent);
            item.setId(i + 1);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.BELOW, below);
            rlp.topMargin = 2;
            rlp.bottomMargin = 2;
            rl.addView(item, rlp);
            below = i + 1;
        }

        try
        {
            adapter.close();
        }
        catch (IOException e)
        {
            ErrorHandler.handleException(e, a_);
        }
        adapter = null;
        return view;
    }

    private Activity a_;
    private Date     from_, to_;
    private String[] catguids_;
    private boolean  catortag_;
}
