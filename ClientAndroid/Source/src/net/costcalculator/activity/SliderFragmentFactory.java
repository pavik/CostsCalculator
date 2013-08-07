
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;

import net.costcalculator.service.CostItem;

public class SliderFragmentFactory
{
    public static final String PATH_STAT         = "statistic";
    public static final String PATH_STAT_DAILY   = "daily";
    public static final String PATH_STAT_WEEKLY  = "weekly";
    public static final String PATH_STAT_MONTHLY = "monthly";
    public static final String PATH_STAT_YEARLY  = "yearly";
    public static final String PATH_STAT_FOREVER = "forever";
    public static final String PATH_STAT_CUSTOM  = "custom";

    public static String getStatPath(String stat)
    {
        return String.format("%s/%s", PATH_STAT, stat);
    }

    public static StatisticFragment createStatisticFragment(Context c,
            String path, ArrayList<CostItem> costitems, ArrayList<Date> expensesdates)
    {
        if (path == null || path.length() == 0)
            throw new IllegalArgumentException("path: " + path);

        String[] el = path.split("/");
        if (el == null || el.length != 2)
            throw new IllegalArgumentException("invalid path: " + path);

        if (!el[0].equals(PATH_STAT))
            throw new IllegalArgumentException("invalid path: " + path);

        final String name = el[1];
        StatisticFragment f = new StatisticFragment();
        f.setCostItems(costitems);
        f.setExpensesDates(expensesdates);
        if (name.equals(PATH_STAT_DAILY))
            f.setPeriod(AdvancedStatisticAdapter.StatisticPeriod.DAY);
        else if (name.equals(PATH_STAT_WEEKLY))
            f.setPeriod(AdvancedStatisticAdapter.StatisticPeriod.WEEK);
        else if (name.equals(PATH_STAT_MONTHLY))
            f.setPeriod(AdvancedStatisticAdapter.StatisticPeriod.MONTH);
        else if (name.equals(PATH_STAT_YEARLY))
            f.setPeriod(AdvancedStatisticAdapter.StatisticPeriod.YEAR);
        else if (name.equals(PATH_STAT_FOREVER))
            f.setPeriod(AdvancedStatisticAdapter.StatisticPeriod.FOREVER);
        else if (name.equals(PATH_STAT_CUSTOM))
        {
            // TODO create custom fragment
            f.setPeriod(AdvancedStatisticAdapter.StatisticPeriod.CUSTOM);
        }
        f.initialize(c);
        return f;
    }
}
