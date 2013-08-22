
package net.costcalculator.adapter;

import java.util.ArrayList;
import java.util.HashSet;

import net.costcalculator.activity.R;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class MultiSelectionAdapter extends BaseAdapter
{
    public MultiSelectionAdapter(Activity a, String[] items,
            ArrayList<Integer> selectedItems, boolean singlechoice)
    {
        a_ = a;
        items_ = items;
        selectedItems_ = selectedItems;
        singlechoice_ = singlechoice;
        checkboxes_ = new HashSet<CheckBox>();
    }

    public void setAll(boolean selected)
    {
        selectedItems_.clear();
        if (selected)
            for (int i = 0; i < items_.length; ++i)
                selectedItems_.add(i);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return items_.length;
    }

    @Override
    public Object getItem(int arg0)
    {
        return items_[arg0];
    }

    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View item = null;
        if (convertView == null)
        {
            item = a_.getLayoutInflater().inflate(R.layout.list_item_checkbox,
                    parent, false);
        }
        else
            item = convertView;

        final CheckBox check = (CheckBox) item.findViewById(R.id.btn_checkbox);
        checkboxes_.add(check);

        check.setText(items_[position]);
        check.setChecked(selectedItems_.contains(position));
        check.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (singlechoice_)
                {
                    for (CheckBox c : checkboxes_)
                        c.setChecked(false);
                    selectedItems_.clear();
                    check.setChecked(true);
                    selectedItems_.add(position);
                }
                else
                {
                    int i = selectedItems_.indexOf(Integer.valueOf(position));
                    if (i >= 0)
                    {
                        check.setChecked(false);
                        selectedItems_.remove(i);
                    }
                    else
                    {
                        check.setChecked(true);
                        selectedItems_.add(position);
                    }
                    if (selall_ != null)
                        selall_.setChecked(selectedItems_.size() == items_.length);
                }
            }
        });

        return item;
    }

    public void setSelAllButton(CheckBox selall)
    {
        selall_ = selall;
    }

    private Activity           a_;
    private String[]           items_;
    private ArrayList<Integer> selectedItems_;
    private boolean            singlechoice_;
    private HashSet<CheckBox>  checkboxes_;
    private CheckBox           selall_;
}
