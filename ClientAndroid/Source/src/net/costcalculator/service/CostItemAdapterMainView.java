/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.HashMap;

import net.costcalculator.activity.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Creates view for cost items adapter.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CostItemAdapterMainView implements AdapterViewBuilder<CostItem>
{
    public CostItemAdapterMainView(Activity a, HashMap<String, Integer> counts)
    {
        a_ = a;
        counts_ = counts;
    }

    public void setCounts(HashMap<String, Integer> counts)
    {
        counts_ = counts;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent,
            CostItem obj)
    {
        View view = null;

        if (convertView == null)
        {
            LayoutInflater inflater = a_.getLayoutInflater();
            view = inflater.inflate(R.layout.grid_cell_expense_item, parent,
                    false);
        }
        else
        {
            view = convertView;
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.cell_image);
        ImageView imageViewOverlay = (ImageView) view
                .findViewById(R.id.cell_image_overlay);
        TextView textView = (TextView) view.findViewById(R.id.cell_text);
        TextView textViewCount = (TextView) view.findViewById(R.id.cell_count);

        imageView.setImageResource(R.drawable.ic_folder);
        imageViewOverlay.setVisibility(View.GONE);
        textView.setText(obj.getName());

        Integer n = counts_.get(obj.getGuid());
        textViewCount.setText(n != null ? n.toString() : "0");

        return view;
    }

    private Activity                 a_;
    private HashMap<String, Integer> counts_;
}
