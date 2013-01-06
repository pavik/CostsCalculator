/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Logic is responsible for setup data on the view and handling user requests
 * 
 * <pre>
 * Usage:
 * {
 *     // create instance
 *     StatisticReportLogic l = new StatisticReportLogic(activity);
 * 
 *     // activity uses logic
 * 
 *     // destroy view
 *     l.release();
 *     l = null;
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class StatisticReportLogic
{
    public StatisticReportLogic(Activity a)
    {
        activity_ = a;
        dailyAdapter_ = new DailyReportAdapter(a);

        // initialize list
        ListView lv = (ListView) activity_
                .findViewById(R.id.lv_expenses_report);
        View header = activity_.getLayoutInflater().inflate(
                R.layout.view_list_header, null);
        TextView tvHeaderText = (TextView) header
                .findViewById(R.id.textViewListHeader);
        tvHeaderText.setText(R.string.s_daily_expenses);
        lv.addHeaderView(header);
        lv.setAdapter(dailyAdapter_);
    }

    public void release()
    {
        if (dailyAdapter_ != null)
        {
            dailyAdapter_.release();
            dailyAdapter_ = null;
        }
    }

    private Activity           activity_;
    private DailyReportAdapter dailyAdapter_;
}
