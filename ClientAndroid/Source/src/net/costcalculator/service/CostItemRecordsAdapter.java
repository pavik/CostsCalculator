/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.costcalculator.util.LOG;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Class is adapter between persistent storage and application logic.
 * 
 * <pre>
 * Usage:
 * {
 *     // create instance
 *     CostItemRecordsAdapter adapter = new CostItemRecordsAdapter(context);
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
public class CostItemRecordsAdapter extends BaseAdapter
{
    public CostItemRecordsAdapter(Context context, long costItemId)
            throws Exception
    {
        context_ = context;

        ci_ = CostItemsService.instance().getCostItemById(costItemId);
        ids_ = CostItemsService.instance().getCostItemRecordIds(ci_);
        records_ = new HashMap<Long, CostItemRecord>();
    }

    public void release()
    {
        context_ = null;
        ci_ = null;
        ids_ = null;
        records_ = null;
    }

    public CostItemRecord addNewCostItemRecord(Date creationTime, double sum,
            String comment, String currency, String tag) throws Exception
    {
        CostItemRecord cir = CostItemsService.instance().createCostItemRecord(
                ci_.getGuid(), creationTime, sum);
        cir.setComment(comment);
        cir.setCurrency(currency);
        cir.setTag(tag);
        CostItemsService.instance().saveCostItemRecord(cir);

        ids_.add(0, cir.getId());
        records_.put(cir.getId(), cir);
        notifyDataSetChanged();
        return cir;
    }

    public String getCostItemName()
    {
        return ci_.getName();
    }

    /*
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount()
    {
        return ids_.size();
    }

    /*
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int index)
    {
        return getCostItemRecord(index);
    }

    public CostItemRecord getCostItemRecord(int index)
    {
        long key = getItemId(index);
        if (records_.containsKey(key))
            return records_.get(key);
        else
            return fetchCostItemRecordById(key);
    }

    /*
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int index)
    {
        return ids_.get(index);
    }

    /*
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView tv = new TextView(context_);
        tv.setText(getCostItemRecord(position).toString());

        return tv;
    }

    private CostItemRecord fetchCostItemRecordById(long id)
    {
        CostItemRecord cir = null;
        try
        {
            cir = CostItemsService.instance().getCostItemRecord(id);
            records_.put(id, cir);
        }
        catch (Exception e)
        {
            LOG.E("Failed to fetch cost item record by id = " + id);
        }

        return cir;
    }

    private ArrayList<Long>               ids_;
    private HashMap<Long, CostItemRecord> records_;

    private CostItem                      ci_;
    private Context                       context_;
}
