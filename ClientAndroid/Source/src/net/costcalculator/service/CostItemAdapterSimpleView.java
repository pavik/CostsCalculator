/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import net.costcalculator.activity.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Creates view for cost items adapter.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CostItemAdapterSimpleView implements
        AdapterViewBuilder<CostItem>
{
    public CostItemAdapterSimpleView(Activity a)
    {
        a_ = a;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent,
            CostItem obj)
    {
        View view = null;

        if (convertView == null)
        {
            LayoutInflater inflater = a_.getLayoutInflater();
            view = inflater.inflate(R.layout.view_expense_cat, parent, false);
        }
        else
        {
            view = convertView;
        }

        TextView textView = (TextView) view.findViewById(R.id.tv_folder_name);
        textView.setText(obj.getName());

        return view;
    }

    private Activity a_;
}
