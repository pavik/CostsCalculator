/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import java.util.ArrayList;

import net.costcalculator.service.ImportStatistic;
import android.app.Activity;
import android.content.res.Resources;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Class provides adapter interface for ImportStatistic.
 * 
 * <pre>
 * Usage:
 * {
 *     &#064;code
 *     // create instance
 *     ImportStatisticAdapter adapter = new ImportStatisticAdapter(context,
 *             statistic);
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
public class ImportStatisticAdapter extends BaseAdapter
{
    public ImportStatisticAdapter(Activity a, ImportStatistic stat)
    {
        context_ = a;
        stat_ = stat;
        list_ = new ArrayList<Pair<String, Integer>>();
        Resources r = context_.getResources();
        colBlack_ = r.getColor(R.color.col_black);
        colRed_ = r.getColor(R.color.col_red);
        if (stat_ != null)
        {
            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_ci_total), stat_.ci_total));
            err1_ = r.getString(R.string.stat_ci_error);
            list_.add(new Pair<String, Integer>(err1_, stat_.ci_errors));
            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_ci_ignored),
                    stat_.ci_ignored_existent));
            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_ci_new), stat_.ci_imported_new));
            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_ci_overwritten),
                    stat_.ci_imported_overwritten));

            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_cir_total), stat_.cir_total));
            err2_ = r.getString(R.string.stat_cir_error);
            list_.add(new Pair<String, Integer>(err2_, stat_.cir_errors));
            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_cir_ignored),
                    stat_.cir_ignored_existent));
            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_cir_new), stat_.cir_imported_new));
            list_.add(new Pair<String, Integer>(r
                    .getString(R.string.stat_cir_overwritten),
                    stat_.cir_imported_overwritten));
        }
    }

    public void release()
    {
        list_ = null;
    }

    @Override
    public int getCount()
    {
        return list_.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return list_.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return pos;
    }

    static class ViewHolder
    {
        public TextView tvName;
        public TextView tvCount;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        View view = null;
        if (convertView == null)
        {
            LayoutInflater inflater = context_.getLayoutInflater();
            view = inflater.inflate(R.layout.view_import_stat_item, parent,
                    false);
            ViewHolder vh = new ViewHolder();
            vh.tvName = (TextView) view.findViewById(R.id.tv_name);
            vh.tvCount = (TextView) view.findViewById(R.id.tv_count);
            view.setTag(vh);
        }
        else
        {
            view = convertView;
        }

        ViewHolder vh = (ViewHolder) view.getTag();
        vh.tvName.setText(list_.get(pos).first);
        vh.tvCount.setText(list_.get(pos).second.toString());

        if (list_.get(pos).second > 0 && (list_.get(pos).first.equals(err1_)
                || list_.get(pos).first.equals(err2_)))
        {
            vh.tvName.setTextColor(colRed_);
            vh.tvCount.setTextColor(colRed_);
        }
        else
        {
            vh.tvName.setTextColor(colBlack_);
            vh.tvCount.setTextColor(colBlack_);
        }

        return view;
    }

    private int                              colBlack_, colRed_;
    private String                           err1_, err2_;
    private Activity                         context_;
    private ImportStatistic                  stat_;
    private ArrayList<Pair<String, Integer>> list_;
}
