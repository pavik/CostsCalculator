/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.ArrayList;
import net.costcalculator.activity.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class is singleton adapter between persistent storage and application logic.
 * 
 * Usage: <code>
 * // create instance
 * CostItemAdapter adapter = new CostItemAdapter(context);
 * 
 * // use adapter
 * adapter.some_method();
 * 
 * // destroy adapter
 * adapter.release();
 * adapter = null;
 * </code>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CostItemAdapter extends BaseAdapter
{
    public CostItemAdapter(Activity context)
    {
        context_ = context;
        costItems_ = CostItemsService.instance().getAllCostItems();
    }

    public void release()
    {
    }

    public void addNewCostItem(String name) throws Exception
    {
        CostItem item = CostItemsService.instance().createCostItem(name);
        costItems_.add(item);
        notifyDataSetChanged();
    }

    /*
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount()
    {
        return costItems_.size();
    }

    /*
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int index)
    {
        return costItems_.get(index);
    }

    public CostItem getCostItem(int index)
    {
        return costItems_.get(index);
    }

    public CostItem findCostItemById(long id)
    {
        for (int i = 0; i < costItems_.size(); ++i)
            if (costItems_.get(i).getId() == id)
                return costItems_.get(i);

        return null;
    }

    /*
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int index)
    {
        return getCostItem(index).getId();
    }

    /*
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View cell = null;

        if (convertView == null)
        {
            LayoutInflater inflater = context_.getLayoutInflater();
            cell = inflater.inflate(R.layout.grid_cell_expense_item, parent,
                    false);
        }
        else
        {
            cell = convertView;
        }

        ImageView imageView = (ImageView) cell.findViewById(R.id.cell_image);
        TextView textView = (TextView) cell.findViewById(R.id.cell_text);

        imageView.setImageResource(R.drawable.ic_folder);
        textView.setText(costItems_.get(position).getName());

        return cell;
    }

    private ArrayList<CostItem>    costItems_;
    private Activity                context_;
}
