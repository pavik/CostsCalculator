/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.costcalculator.activity.R;
import net.costcalculator.util.LOG;

import android.app.Activity;
import android.view.LayoutInflater;
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
public class CostItemRecordsAdapter extends BaseAdapter implements Closeable
{
    public CostItemRecordsAdapter(Activity context, long costItemId)
            throws Exception
    {
        context_ = context;
        cis_ = new CostItemsService(context_);
        ci_ = cis_.getCostItemById(costItemId);
        ids_ = cis_.getCostItemRecordIds(ci_, null, null);
        records_ = new HashMap<Long, CostItemRecord>();
        higlightback_ = true;
    }

    public CostItemRecordsAdapter(Activity context, String guidtag, Date from,
            Date to, boolean catortag)
    {
        context_ = context;
        cis_ = new CostItemsService(context_);
        if (catortag)
        {
            ci_ = cis_.getCostItemByGUID(guidtag);
            ids_ = cis_.getCostItemRecordIds(ci_, from, to);
        }
        else
            ids_ = cis_.getCostItemRecordIdsByTag(guidtag, from, to);
        records_ = new HashMap<Long, CostItemRecord>();
        higlightback_ = false;
    }

    @Override
    public void close() throws IOException
    {
        release();
    }

    public void release()
    {
        if (cis_ != null)
            cis_.release();
        cis_ = null;
        ci_ = null;
        ids_ = null;
        records_ = null;
    }

    public ArrayList<CostItem> getAllCostItems()
    {
        if (costitems_ == null)
            costitems_ = cis_.getAllCostItems();
        return costitems_;
    }

    public CostItemRecord addNewCostItemRecord(Date creationTime, double sum,
            String comment, String currency, String tag) throws Exception
    {
        CostItemRecord cir = cis_.createCostItemRecord(ci_.getGuid(),
                creationTime, sum);
        cir.setComment(comment);
        cir.setCurrency(currency);
        cir.setTag(tag);
        cis_.saveCostItemRecord(cir);

        ids_.add(0, cir.getId());
        records_.put(cir.getId(), cir);
        notifyDataSetChanged();
        return cir;
    }

    public CostItemRecord getLatestCostItemRecordByDate(long costItemId)
            throws Exception
    {
        return cis_.getLatestCostItemRecordByDate(costItemId);
    }

    public void updateCostItemRecord(long id)
    {
        CostItemRecord cir = fetchCostItemRecordById(id);
        if (cir != null)
            notifyDataSetChanged();
        else
            LOG.E("CostItemRecordsAdapter::updateCostItemRecord - failed with id = "
                    + id);
    }

    public void newCostItemRecord(long id)
    {
        try
        {
            CostItemRecord cir = cis_.getCostItemRecord(id);
            ids_.add(0, cir.getId());
            records_.put(cir.getId(), cir);
            notifyDataSetChanged();
        }
        catch (Exception e)
        {
            LOG.E("newCostItemRecord - id = " + id);
            LOG.E(e.getMessage());
        }
    }

    public void moveExpense(CostItemRecord item, CostItem cat) throws Exception
    {
        if (item != null && cat != null)
        {
            if (cis_.moveCostItemRecord(item, cat))
            {
                for (int i = 0; i < ids_.size(); ++i)
                    if (ids_.get(i) == item.getId())
                    {
                        ids_.remove(i);
                        records_.remove(item.getId());
                        break;
                    }
                notifyDataSetChanged();
            }
        }
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

    public CostItemRecord getCostItemRecord(long id)
    {
        if (records_.containsKey(id))
            return records_.get(id);
        else
            return fetchCostItemRecordById(id);
    }

    public CostItemRecord getCostItemRecord(int index)
    {
        long key = getItemId(index);
        if (records_.containsKey(key))
            return records_.get(key);
        else
            return fetchCostItemRecordById(key);
    }

    public void deletePosition(int pos) throws Exception
    {
        if (pos >= 0 && pos < ids_.size())
        {
            CostItemRecord cir = getCostItemRecord(pos);
            ids_.remove(cir.getId());
            records_.remove(cir.getId());
            cis_.deleteCostItemRecord(cir);
            notifyDataSetChanged();
        }
    }

    /*
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int index)
    {
        return ids_.get(index);
    }

    static class ViewHolder
    {
        public TextView tvPrice;
        public TextView tvDate;
        public View     vDiv;
        public TextView tvComment;
        public TextView tvTag;
    }

    /*
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View item = null;

        if (convertView == null)
        {
            LayoutInflater inflater = context_.getLayoutInflater();
            item = inflater.inflate(R.layout.view_price_list_item, parent,
                    false);
            if (!higlightback_)
                item.setBackgroundResource(R.drawable.background_expense_item);
            ViewHolder vh = new ViewHolder();
            vh.tvPrice = (TextView) item.findViewById(R.id.tv_price_val);
            vh.tvDate = (TextView) item.findViewById(R.id.tv_date_val);
            vh.vDiv = item.findViewById(R.id.v_price_div);
            vh.tvComment = (TextView) item.findViewById(R.id.tv_comment);
            vh.tvTag = (TextView) item.findViewById(R.id.tv_tag);
            item.setTag(vh);
        }
        else
        {
            item = convertView;
        }

        ViewHolder vh = (ViewHolder) item.getTag();
        CostItemRecord cir = getCostItemRecord(position);
        vh.tvPrice.setText(DataFormatService.formatPrice(cir.getSum()) + " "
                + cir.getCurrency());
        vh.tvDate.setText(DataFormatService.formatDateTime(cir
                .getCreationTime()));
        vh.tvComment.setText(cir.getComment());
        vh.tvTag.setText(cir.getTag());

        if (cir.getComment().length() > 0 || cir.getTag().length() > 0)
        {
            vh.vDiv.setVisibility(View.VISIBLE);
            vh.tvComment
                    .setVisibility(cir.getComment().length() > 0 ? View.VISIBLE
                            : View.GONE);
            vh.tvTag.setVisibility(cir.getTag().length() > 0 ? View.VISIBLE
                    : View.GONE);
        }
        else
        {
            vh.vDiv.setVisibility(View.GONE);
            vh.tvComment.setVisibility(View.GONE);
            vh.tvTag.setVisibility(View.GONE);
        }

        return item;
    }

    private CostItemRecord fetchCostItemRecordById(long id)
    {
        CostItemRecord cir = null;
        try
        {
            cir = cis_.getCostItemRecord(id);
            records_.put(id, cir);
        }
        catch (Exception e)
        {
            LOG.E("Failed to fetch cost item record by id = " + id);
            LOG.E(e.getMessage());
        }

        return cir;
    }

    private ArrayList<Long>               ids_;
    private HashMap<Long, CostItemRecord> records_;
    private CostItemsService              cis_;
    private CostItem                      ci_;
    private ArrayList<CostItem>           costitems_;
    private Activity                      context_;
    private boolean                       higlightback_;
}
