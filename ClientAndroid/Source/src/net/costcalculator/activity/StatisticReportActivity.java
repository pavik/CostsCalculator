/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;

/**
 * Statistic report screen.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class StatisticReportActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_report);
        LOG.T("StatisticReportActivity::onCreate");
        
        logic_ = new StatisticReportLogic(this);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LOG.T("StatisticReportActivity::onDestroy");

        if (logic_ != null)
        {
            logic_.release();
            logic_ = null;
        }
    }
    
    private StatisticReportLogic logic_;
}
