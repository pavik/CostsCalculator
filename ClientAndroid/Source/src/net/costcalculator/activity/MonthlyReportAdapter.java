/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemsService;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.service.StatisticReportItem;
import net.costcalculator.util.LOG;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Class is adapter between persistent storage and application logic.
 * 
 * <pre>
 * Usage:
 * {
 *     &#064;code
 *     // create instance
 *     MonthlyReportAdapter adapter = new MonthlyReportAdapter(context);
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
public class MonthlyReportAdapter extends BaseAdapter
{
    public MonthlyReportAdapter(Activity a)
    {
        context_ = a;
        months_ = CostItemsService.instance().getExpensesMonths();
        costItemName_ = new Hashtable<String, String>();
        ArrayList<CostItem> ci = CostItemsService.instance().getAllCostItems();
        for (int i = 0; i < ci.size(); ++i)
            costItemName_.put(ci.get(i).getGuid(), ci.get(i).getName());
    }

    public void release()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount()
    {
        return months_.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int i)
    {
        return months_.get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }

    static class ViewHolder
    {
        public TextView tvPrice;
        public TextView tvDate;
        public View     vDivider;
    }

    /*
     * (non-Javadoc)
     * 
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
            item = inflater.inflate(R.layout.view_report_item, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.tvPrice = (TextView) item.findViewById(R.id.tv_price_val);
            vh.tvDate = (TextView) item.findViewById(R.id.tv_date_val);
            vh.vDivider = (View) item.findViewById(R.id.v_divider);
            item.setTag(vh);
        }
        else
        {
            item = convertView;
        }

        ViewHolder vh = (ViewHolder) item.getTag();
        ArrayList<StatisticReportItem> report = getReportItem(position);
        vh.tvDate.setText(getReportMonth(report));
        vh.tvPrice.setText(getReportPrice(report));

        LinearLayout ll = new LinearLayout(context_);
        ll.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = context_.getLayoutInflater();
        for (int i = 0; i < report.size(); ++i)
        {
            StatisticReportItem repItem = report.get(i);
            View repItemView = inflater.inflate(R.layout.view_sub_report_item,
                    parent, false);
            TextView tvSubPrice = (TextView) repItemView
                    .findViewById(R.id.tv_price_val);
            TextView tvSubCat = (TextView) repItemView
                    .findViewById(R.id.tv_cat_val);
            tvSubCat.setText(costItemName_.get(repItem.guid).toString() + " ("
                    + repItem.count + ")");
            tvSubPrice.setText(DataFormatService.formatPrice(repItem.sum) + " "
                    + repItem.currency);
            ll.addView(repItemView);
        }

        RelativeLayout rl = (RelativeLayout) item;
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, vh.vDivider.getId());
        rl.addView(ll, rlp);

        return item;
    }

    private String getReportMonth(ArrayList<StatisticReportItem> report)
    {
        if (report.size() > 0)
            return DataFormatService.formatMonth(report.get(0).dateFrom);
        else
            return "not a date";
    }

    private String getReportPrice(ArrayList<StatisticReportItem> report)
    {
        TreeMap<String, Double> totals = new TreeMap<String, Double>();
        for (int i = 0; i < report.size(); ++i)
        {
            Double value = totals.get(report.get(i).currency);
            if (value == null)
                value = report.get(i).sum;
            else
                value += report.get(i).sum;

            totals.put(report.get(i).currency, value);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : totals.entrySet())
        {
            String key = entry.getKey();
            Double value = entry.getValue();

            sb.append(DataFormatService.formatPrice(value) + " " + key + "\n");
        }

        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private ArrayList<StatisticReportItem> getReportItem(int position)
    {
        ArrayList<StatisticReportItem> report = new ArrayList<StatisticReportItem>();
        Date date = months_.get(position);
        try
        {
            report = CostItemsService.instance().getStatisticReport(date, endOfMonth(date));
        }
        catch (Exception e)
        {
            LOG.E("DailyReportAdapter::getReportItem(" + position + ") failed");
            LOG.E(e.getMessage());
        }

        return report;
    }
    
    private Date endOfMonth(Date d)
    {
        Date res = new Date(d.getTime());
        Calendar c = Calendar.getInstance();
        c.set(d.getYear(), d.getMonth(), d.getDate());
        res.setDate(c.getActualMaximum(Calendar.DAY_OF_MONTH));
        res.setHours(23);
        res.setMinutes(59);
        res.setSeconds(59);
        return res;
    }

    private Activity                  context_;
    private ArrayList<Date>           months_;
    private Hashtable<String, String> costItemName_;
}