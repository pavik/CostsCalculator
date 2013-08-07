
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
import net.costcalculator.util.DateUtil;
import net.costcalculator.util.ErrorHandler;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdvancedStatisticAdapter extends BaseAdapter
{
    public enum StatisticPeriod
    {
        DAY, WEEK, MONTH, YEAR, FOREVER, CUSTOM
    }

    public AdvancedStatisticAdapter(Context c, Date from, Date to,
            StatisticPeriod type, int interval, ArrayList<CostItem> items,
            ArrayList<Date> expensesdates)
    {
        context_ = c;
        periodType_ = type;
        service_ = new CostItemsService(context_);
        inflater_ = (LayoutInflater) context_
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        period_ = getStatisticPeriods(expensesdates, from, to, type, interval);
        guid2name_ = new Hashtable<String, String>();
        for (int i = 0; i < items.size(); ++i)
            guid2name_.put(items.get(i).getGuid(), items.get(i).getName());
    }

    public AdvancedStatisticAdapter()
    {
        period_ = new ArrayList<Pair<Date, Date>>();
    }

    public void close()
    {
        if (service_ != null)
        {
            service_.release();
            service_ = null;
        }
        if (period_ != null)
            period_ = null;
        if (guid2name_ != null)
            guid2name_ = null;
    }

    public void setFilter(ArrayList<CostItem> items)
    {
        guid2name_ = new Hashtable<String, String>();
        for (int i = 0; i < items.size(); ++i)
            guid2name_.put(items.get(i).getGuid(), items.get(i).getName());
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return period_.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return period_.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return pos;
    }

    static class ViewHolder
    {
        public TextView     tvPrice;
        public TextView     tvDate;
        public View         vDivider;
        public LinearLayout ll;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View item = null;

        if (convertView == null)
        {
            item = inflater_.inflate(R.layout.view_report_item, parent, false);
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
        vh.tvDate.setText(getReportPeriod(position));
        vh.tvPrice.setText(getReportPrice(report));

        LinearLayout ll = new LinearLayout(context_);
        ll.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < report.size(); ++i)
        {
            StatisticReportItem repItem = report.get(i);
            View repItemView = inflater_.inflate(R.layout.view_sub_report_item,
                    parent, false);
            TextView tvSubPrice = (TextView) repItemView
                    .findViewById(R.id.tv_price_val);
            TextView tvSubCat = (TextView) repItemView
                    .findViewById(R.id.tv_cat_val);
            tvSubCat.setText(getNameByGUID(repItem.guid) + " (" + repItem.count
                    + ")");
            tvSubPrice.setText(DataFormatService.formatPrice(repItem.sum) + " "
                    + repItem.currency);
            ll.addView(repItemView);
        }

        RelativeLayout rl = (RelativeLayout) item;
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.BELOW, vh.vDivider.getId());

        if (vh.ll != null)
            rl.removeView(vh.ll);
        rl.addView(ll, rlp);
        vh.ll = ll;

        return item;
    }

    private ArrayList<Pair<Date, Date>> getStatisticPeriods(
            ArrayList<Date> expensesdates, Date from, Date to,
            StatisticPeriod type, int interval)
    {
        switch (type)
        {
        case DAY:
            return divideByDays(expensesdates);
        case WEEK:
            return divideByPeriods(expensesdates, Calendar.WEEK_OF_YEAR);
        case MONTH:
            return divideByPeriods(expensesdates, Calendar.MONTH);
        case YEAR:
            return divideByPeriods(expensesdates, Calendar.YEAR);
        case CUSTOM:
            return divideByIntervals(expensesdates, from, to, interval);
        case FOREVER:
            ArrayList<Pair<Date, Date>> periods = new ArrayList<Pair<Date, Date>>();
            final int len = expensesdates.size();
            if (len > 0)
                periods.add(new Pair<Date, Date>(expensesdates.get(len - 1),
                        expensesdates.get(0)));
            return periods;
        }
        return new ArrayList<Pair<Date, Date>>();
    }

    private ArrayList<Pair<Date, Date>> divideByPeriods(
            ArrayList<Date> expensesdates, int periodtype)
    {
        ArrayList<Pair<Date, Date>> periods = new ArrayList<Pair<Date, Date>>();
        ArrayList<Date> dates = new ArrayList<Date>();
        for (int i = 0; i < expensesdates.size(); ++i)
        {
            if (dates.isEmpty())
                dates.add(expensesdates.get(i));
            else
            {
                if (DateUtil.samePeriod(expensesdates.get(i), dates.get(0),
                        periodtype))
                    dates.add(expensesdates.get(i));
                else
                {
                    periods.add(new Pair<Date, Date>(
                            dates.get(dates.size() - 1), dates.get(0)));
                    dates.clear();
                    dates.add(expensesdates.get(i));
                }
            }
        }

        if (!dates.isEmpty())
            periods.add(new Pair<Date, Date>(dates.get(dates.size() - 1), dates
                    .get(0)));
        return periods;
    }

    private ArrayList<Pair<Date, Date>> divideByIntervals(
            ArrayList<Date> expensesdates, Date from, Date to, int interval)
    {
        ArrayList<Pair<Date, Date>> periods = new ArrayList<Pair<Date, Date>>();
        if (interval < 1)
            return periods;

        ArrayList<Date> dates = new ArrayList<Date>();
        for (int i = 0; i < expensesdates.size(); ++i)
        {
            if (to != null && expensesdates.get(i).getTime() > to.getTime())
                continue;
            if (from != null && expensesdates.get(i).getTime() < from.getTime())
                continue;

            if (dates.isEmpty())
                dates.add(expensesdates.get(i));
            else
            {
                if (DateUtil.getDaysCount(expensesdates.get(i), dates.get(0)) >= interval)
                {
                    periods.add(new Pair<Date, Date>(
                            dates.get(dates.size() - 1), dates.get(0)));
                    dates.clear();
                    dates.add(expensesdates.get(i));
                }
                else
                    dates.add(expensesdates.get(i));
            }
        }

        if (!dates.isEmpty())
            periods.add(new Pair<Date, Date>(dates.get(dates.size() - 1), dates
                    .get(0)));

        return periods;
    }

    private ArrayList<Pair<Date, Date>> divideByDays(
            ArrayList<Date> expensesdates)
    {
        ArrayList<Pair<Date, Date>> periods = new ArrayList<Pair<Date, Date>>();
        for (int i = 0; i < expensesdates.size(); ++i)
            periods.add(new Pair<Date, Date>(expensesdates.get(i),
                    expensesdates.get(i)));
        return periods;
    }

    private ArrayList<StatisticReportItem> getReportItem(int pos)
    {
        ArrayList<StatisticReportItem> report = new ArrayList<StatisticReportItem>();
        Pair<Date, Date> period = period_.get(pos);
        try
        {
            // there is no caching to prevent consuming of memory
            report = service_.getStatisticReport(period.first, period.second);
        }
        catch (Exception e)
        {
            ErrorHandler.handleException(e, context_);
        }

        ArrayList<StatisticReportItem> filteredreport = new ArrayList<StatisticReportItem>();
        for (int i = 0; i < report.size(); ++i)
        {
            if (guid2name_.containsKey(report.get(i).guid))
                filteredreport.add(report.get(i));
        }

        return filteredreport;
    }

    private String getReportPeriod(int pos)
    {
        Pair<Date, Date> period = period_.get(pos);
        if (periodType_ == StatisticPeriod.DAY)
            return DataFormatService.formatDate(period.first);
        else if (periodType_ == StatisticPeriod.WEEK)
            return DataFormatService.formatWeek(period.first, period.second);
        else if (periodType_ == StatisticPeriod.MONTH)
            return DataFormatService.formatMonth(period.first, period.second);
        else if (periodType_ == StatisticPeriod.YEAR)
            return DataFormatService.formatYear(period.first, period.second);
        else if (periodType_ == StatisticPeriod.FOREVER)
            return DataFormatService.formatCustom(period.first, period.second);
        else if (periodType_ == StatisticPeriod.CUSTOM)
            return DataFormatService.formatCustom(period.first, period.second);
        else
            return "undefined";
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

    private String getNameByGUID(String guid)
    {
        String val = guid2name_.get(guid);
        if (val != null)
            return val;
        else
            return "-";
    }

    private Context                     context_;
    private StatisticPeriod             periodType_;
    private CostItemsService            service_;
    private LayoutInflater              inflater_;
    private ArrayList<Pair<Date, Date>> period_;
    private Hashtable<String, String>   guid2name_;
}