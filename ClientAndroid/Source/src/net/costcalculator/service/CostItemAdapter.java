/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.ArrayList;
import java.util.HashMap;

import net.costcalculator.activity.R;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class is adapter between persistent storage and application logic.
 * 
 * <pre>
 * Usage:
 * {
 *     &#064;code
 *     // create instance
 *     CostItemAdapter adapter = new CostItemAdapter(context);
 * 
 *     // use adapter
 *     adapter.some_method();
 * 
 *     // destroy adapter
 *     adapter.release();
 *     adapter = null;
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class CostItemAdapter extends BaseAdapter
{
    public CostItemAdapter(Activity context) throws Exception
    {
        context_ = context;
        cis_ = new CostItemsService(context_);
        costItems_ = cis_.getAllCostItems();
        if (costItems_.isEmpty())
            setup_basic_items();
    }

    public void release()
    {
        cis_.release();
        cis_ = null;
        costItems_ = null;
    }

    public void addNewCostItem(String name) throws Exception
    {
        CostItem item = cis_.createCostItem(name);
        costItems_.add(item);
        notifyDataSetChanged();
    }

    public void changeName(String name, int pos) throws Exception
    {
        if (name.length() > 0 && pos >= 0 && pos < costItems_.size())
        {
            CostItem ci = costItems_.get(pos);
            ci.setName(name);
            cis_.saveCostItem(ci);
            notifyDataSetChanged();
        }
    }

    public void deletePosition(int pos) throws Exception
    {
        if (pos >= 0 && pos < costItems_.size())
        {
            CostItem ci = costItems_.get(pos);
            cis_.deleteCostItemRecords(ci);
            cis_.deleteCostItem(ci);
            costItems_.remove(pos);
            notifyDataSetChanged();
        }
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
        ImageView imageViewOverlay = (ImageView) cell
                .findViewById(R.id.cell_image_overlay);
        TextView textView = (TextView) cell.findViewById(R.id.cell_text);
        TextView textViewCount = (TextView) cell.findViewById(R.id.cell_count);

        imageView.setImageResource(R.drawable.ic_folder);
        imageViewOverlay.setVisibility(View.GONE);
        textView.setText(costItems_.get(position).getName());

        HashMap<String, Integer> m = cis_.getCostItemRecordsCount();
        if (m != null)
        {
            Integer n = m.get(costItems_.get(position).getGuid());
            textViewCount.setText(n != null ? n.toString() : "0");
        }

        return cell;
    }

    private void setup_basic_items() throws NotFoundException, Exception
    {
        Resources r = context_.getResources();
        addNewCostItem(r.getString(R.string.ci_food));
        addNewCostItem(r.getString(R.string.ci_household));
        addNewCostItem(r.getString(R.string.ci_clothes));
        addNewCostItem(r.getString(R.string.ci_accommodation));
        addNewCostItem(r.getString(R.string.ci_credit));
        addNewCostItem(r.getString(R.string.ci_car));
        addNewCostItem(r.getString(R.string.ci_health));
        addNewCostItem(r.getString(R.string.ci_restaurant));
        addNewCostItem(r.getString(R.string.ci_mobile));
        addNewCostItem(r.getString(R.string.ci_entertainment));
        addNewCostItem(r.getString(R.string.ci_other));
    }

    private ArrayList<CostItem> costItems_;
    private Activity            context_;
    private CostItemsService    cis_;
}
