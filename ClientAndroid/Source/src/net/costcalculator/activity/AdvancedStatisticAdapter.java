
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import net.costcalculator.service.CostItem;
import net.costcalculator.service.CostItemsService;
import net.costcalculator.service.DataFormatService;
import net.costcalculator.service.CategoriesReportItem;
import net.costcalculator.service.ReportItem;
import net.costcalculator.service.TagsReportItem;
import net.costcalculator.util.DateUtil;
import net.costcalculator.util.ErrorHandler;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdvancedStatisticAdapter extends BaseAdapter
{
    public static final int REPORT_CAT = 1;
    public static final int REPORT_TAG = 2;

    public enum StatisticPeriod
    {
        DAY, WEEK, MONTH, YEAR, FOREVER, CUSTOM
    }

    public AdvancedStatisticAdapter(Context c, Date from, Date to,
            StatisticPeriod type, int interval, ArrayList<CostItem> items,
            ArrayList<Date> expensesdates, int reportType)
    {
        context_ = c;
        reportType_ = reportType;
        periodType_ = type;
        service_ = new CostItemsService(context_);
        inflater_ = (LayoutInflater) context_
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        period_ = getStatisticPeriods(expensesdates, from, to, type, interval);
        guid2name_ = new Hashtable<String, String>();
        for (int i = 0; i < items.size(); ++i)
            guid2name_.put(items.get(i).getGuid(), items.get(i).getName());
        tags_ = new HashSet<String>();
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
        if (tags_ != null)
            tags_ = null;
    }

    public void setFilter(ArrayList<CostItem> items)
    {
        guid2name_ = new Hashtable<String, String>();
        for (int i = 0; i < items.size(); ++i)
            guid2name_.put(items.get(i).getGuid(), items.get(i).getName());
        notifyDataSetChanged();
    }

    public void setTagFilter(ArrayList<String> tags)
    {
        tags_ = new HashSet<String>();
        for (int i = 0; i < tags.size(); ++i)
            tags_.add(tags.get(i));
        notifyDataSetChanged();
    }

    public void setCustomInterval(Date from, Date to, StatisticPeriod type,
            int interval, ArrayList<Date> expensesdates)
    {
        period_ = getStatisticPeriods(expensesdates, from, to, type, interval);
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
        public ImageView    imgLink;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View item = null;

        if (convertView == null)
        {
            item = inflater_.inflate(R.layout.view_report_item, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.tvPrice = (TextView) item.findViewById(R.id.tv_price_val);
            vh.tvDate = (TextView) item.findViewById(R.id.tv_date_val);
            vh.vDivider = (View) item.findViewById(R.id.v_divider);
            vh.imgLink = (ImageView) item.findViewById(R.id.img_link);
            item.setTag(vh);
        }
        else
        {
            item = convertView;
        }

        ViewHolder vh = (ViewHolder) item.getTag();
        final ArrayList<ReportItem> report = getReportItem(position);
        final String periodstr = getReportPeriod(position);
        SpannableString periodss = new SpannableString(periodstr);
        periodss.setSpan(new UnderlineSpan(), 0, periodstr.length(), 0);
        vh.tvDate.setText(periodss);
        vh.tvPrice.setText(getReportPrice(report));
        OnClickListener listener = new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(context_,
                        StatisticDetailsActivity.class);
                Pair<Date, Date> period = period_.get(position);
                intent.putExtra(StatisticDetailsActivity.EXTRA_DATE_FROM,
                        period.first.getTime());
                intent.putExtra(StatisticDetailsActivity.EXTRA_DATE_TO,
                        period.second.getTime());

                String[] guidtag = new String[report.size()];
                String[] names = new String[report.size()];
                for (int i = 0; i < report.size(); ++i)
                {
                    names[i] = report.get(i).getTitle();
                    if (report.get(i) instanceof CategoriesReportItem)
                        guidtag[i] = ((CategoriesReportItem) report.get(i))
                                .getGuid();
                    else
                        guidtag[i] = report.get(i).getTitle();
                }
                intent.putExtra(StatisticDetailsActivity.EXTRA_GUIDTAG, guidtag);
                intent.putExtra(StatisticDetailsActivity.EXTRA_CAT_OR_TAG,
                        reportType_ == REPORT_CAT);
                intent.putExtra(StatisticDetailsActivity.EXTRA_CATNAME, names);
                context_.startActivity(intent);
            }
        };
        vh.tvDate.setOnClickListener(listener);
        vh.imgLink.setOnClickListener(listener);

        LinearLayout ll = new LinearLayout(context_);
        ll.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < report.size(); ++i)
        {
            ReportItem repItem = report.get(i);
            View repItemView = inflater_.inflate(R.layout.view_sub_report_item,
                    parent, false);
            TextView tvSubPrice = (TextView) repItemView
                    .findViewById(R.id.tv_price_val);
            TextView tvSubCat = (TextView) repItemView
                    .findViewById(R.id.tv_cat_val);
            tvSubCat.setText(repItem.getTitle() + " (" + repItem.getCount()
                    + ")");
            tvSubPrice.setText(DataFormatService.formatPrice(repItem.getSum())
                    + " " + repItem.getCurrency());
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
        if (interval == 0 && to != null && from != null)
        {
            periods.add(new Pair<Date, Date>(from, to));
            return periods;
        }
        else if (interval < 0 || from == null || to == null)
            return periods;

        from.setHours(0);
        from.setMinutes(0);
        from.setSeconds(0);
        to.setHours(23);
        to.setMinutes(59);
        to.setSeconds(59);
        while (from.getTime() < to.getTime())
        {
            Date middle = new Date(to.getTime() - (interval - 1) * 86400000);
            if (middle.getTime() < from.getTime())
                middle = from;
            periods.add(new Pair<Date, Date>(middle, to));
            to = new Date(middle.getTime() - 86400000);
        }

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

    private ArrayList<ReportItem> getReportItem(int pos)
    {
        ArrayList<ReportItem> filteredreport = new ArrayList<ReportItem>();

        if (reportType_ == REPORT_CAT)
        {
            ArrayList<CategoriesReportItem> report = new ArrayList<CategoriesReportItem>();
            Pair<Date, Date> period = period_.get(pos);
            try
            {
                // there is no caching to prevent consuming of memory
                report = service_.getStatisticReportByCategories(period.first,
                        period.second);
            }
            catch (Exception e)
            {
                ErrorHandler.handleException(e, context_);
            }

            for (int i = 0; i < report.size(); ++i)
            {
                if (guid2name_.containsKey(report.get(i).getGuid()))
                {
                    CategoriesReportItem item = report.get(i);
                    item.setTitle(getNameByGUID(item.getGuid()));
                    filteredreport.add(item);
                }
            }
        }
        else if (reportType_ == REPORT_TAG)
        {
            ArrayList<TagsReportItem> report = new ArrayList<TagsReportItem>();
            Pair<Date, Date> period = period_.get(pos);
            try
            {
                // there is no caching to prevent consuming of memory
                report = service_.getStatisticReportByTags(period.first,
                        period.second);
            }
            catch (Exception e)
            {
                ErrorHandler.handleException(e, context_);
            }

            for (int i = 0; i < report.size(); ++i)
                if (tags_.contains(report.get(i).getTitle()))
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

    private String getReportPrice(ArrayList<ReportItem> report)
    {
        TreeMap<String, Double> totals = new TreeMap<String, Double>();
        for (int i = 0; i < report.size(); ++i)
        {
            Double value = totals.get(report.get(i).getCurrency());
            if (value == null)
                value = report.get(i).getSum();
            else
                value += report.get(i).getSum();

            totals.put(report.get(i).getCurrency(), value);
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
    private int                         reportType_;
    private StatisticPeriod             periodType_;
    private CostItemsService            service_;
    private LayoutInflater              inflater_;
    private ArrayList<Pair<Date, Date>> period_;
    private Hashtable<String, String>   guid2name_;
    private HashSet<String>             tags_;
}
