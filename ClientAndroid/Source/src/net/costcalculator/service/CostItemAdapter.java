/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.ArrayList;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
    public CostItemAdapter(Activity context, AdapterViewBuilder<CostItem> vb)
            throws Exception
    {
        context_ = context;
        viewbuilder_ = vb;
        cis_ = new CostItemsService(context_);
        costItems_ = cis_.getAllCostItems();
    }

    public void release()
    {
        cis_.release();
        cis_ = null;
        costItems_ = null;
    }

    public void reload()
    {
        costItems_ = cis_.getAllCostItems();
        notifyDataSetChanged();
    }

    public void refreshView()
    {
        notifyDataSetChanged();
    }

    public void addNewCostItem(String name) throws Exception
    {
        if (name != null && name.length() > 0)
        {
            CostItem item = cis_.createCostItem(name);
            costItems_.add(item);
            notifyDataSetChanged();
        }
    }

    public void changeName(String name, int pos) throws Exception
    {
        if (name != null && name.length() > 0 && pos >= 0
                && pos < costItems_.size())
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

    public void moveExpenses(int fromCat, int toCat)
    {
        CostItem from = null, to = null;
        if (toCat >= 0 && toCat < costItems_.size())
            to = costItems_.get(toCat);
        if (fromCat >= 0 && fromCat < costItems_.size())
            from = costItems_.get(fromCat);
        if (from != null && to != null)
            cis_.moveCostItems(from, to);
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

    public CostItem getCostItem(long id)
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
        return viewbuilder_.getView(position, convertView, parent,
                costItems_.get(position));
    }

    private ArrayList<CostItem>          costItems_;
    private Activity                     context_;
    private CostItemsService             cis_;
    private AdapterViewBuilder<CostItem> viewbuilder_;
}
