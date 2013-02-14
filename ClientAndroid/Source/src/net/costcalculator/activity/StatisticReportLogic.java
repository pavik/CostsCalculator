/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
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
public class StatisticReportLogic implements OnClickListener
{
    public static final int DAILY_VIEW   = 1;
    public static final int MONTHLY_VIEW = 2;

    public StatisticReportLogic(Activity a)
    {
        activity_ = a;
        viewMode_ = DAILY_VIEW;
        dailyAdapter_ = new DailyReportAdapter(a);
        monthlyAdapter_ = new MonthlyReportAdapter(a);

        // initialize list
        lvHistory_ = (ListView) activity_.findViewById(R.id.lv_expenses_report);
        View header = activity_.getLayoutInflater().inflate(
                R.layout.view_report_header, null);
        tvHeaderText_ = (TextView) header.findViewById(R.id.textViewListHeader);
        tvHeaderText_.setText(R.string.s_daily_expenses);
        imgArrow_ = (ImageView) header
                .findViewById(R.id.img_report_header_arrow);
        imgArrow_.setOnClickListener(this);
        lvHistory_.addHeaderView(header);
        lvHistory_.setAdapter(dailyAdapter_);
        lvHistory_.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    onTouchDown(event.getRawX(), event.getRawY());
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    onTouchUp(event.getRawX(), event.getRawY());
                return false;
            }
        });
        sin70_ = Math.sin(Math.toRadians(70.0));
    }

    public void release()
    {
        if (dailyAdapter_ != null)
        {
            dailyAdapter_.release();
            dailyAdapter_ = null;
        }
        if (monthlyAdapter_ != null)
        {
            monthlyAdapter_.release();
            monthlyAdapter_ = null;
        }
    }

    @Override
    public void onClick(View v)
    {
        if (imgArrow_ != null && imgArrow_.getId() == v.getId())
            nextPage();
    }

    private void nextPage()
    {
        if (viewMode_ == DAILY_VIEW)
        {
            lvHistory_.setAdapter(monthlyAdapter_);
            viewMode_ = MONTHLY_VIEW;
        }
        else
        {
            lvHistory_.setAdapter(dailyAdapter_);
            viewMode_ = DAILY_VIEW;
        }

        tvHeaderText_
                .setText(viewMode_ == DAILY_VIEW ? R.string.s_daily_expenses
                        : R.string.s_monthly_expenses);
    }

    private void onTouchDown(float x, float y)
    {
        touchDownX_ = x;
        touchDownY_ = y;
    }

    private void onTouchUp(float x, float y)
    {
        float touchUpX = x;
        float touchUpY = y;
        float a = Math.abs(touchDownX_ - touchUpX);
        float b = Math.abs(touchDownY_ - touchUpY);
        double c = Math.sqrt(a*a + b*b);
        if (c < 10.0)
            return;
        else if (a / c > sin70_)
            nextPage();
    }

    private final double         sin70_;
    private float                touchDownX_;
    private float                touchDownY_;
    private int                  viewMode_;
    private ImageView            imgArrow_;
    private TextView             tvHeaderText_;
    private ListView             lvHistory_;
    private Activity             activity_;
    private DailyReportAdapter   dailyAdapter_;
    private MonthlyReportAdapter monthlyAdapter_;
}
