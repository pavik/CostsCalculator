
package net.costcalculator.activity;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;

import net.costcalculator.service.CostItem;

public class SliderFragmentFactory
{
    public static final String PATH_CAT          = "cat";
    public static final String PATH_TAG          = "tag";
    public static final String PATH_STAT_DAILY   = "daily";
    public static final String PATH_STAT_WEEKLY  = "weekly";
    public static final String PATH_STAT_MONTHLY = "monthly";
    public static final String PATH_STAT_YEARLY  = "yearly";
    public static final String PATH_STAT_FOREVER = "forever";
    public static final String PATH_STAT_CUSTOM  = "custom";

    public static String getCatPath(String stat)
    {
        return String.format("%s/%s", PATH_CAT, stat);
    }

    public static String getTagPath(String stat)
    {
        return String.format("%s/%s", PATH_TAG, stat);
    }

    public static StatisticFragment createStatisticFragment(Context c,
            String path, ArrayList<CostItem> costitems,
            ArrayList<Date> expensesdates, ArrayList<Date> expensestagdates,
            ArrayList<String> tags)
    {
        if (path == null || path.length() == 0)
            throw new IllegalArgumentException("path: " + path);

        String[] el = path.split("/");
        if (el == null || el.length != 2)
            throw new IllegalArgumentException("invalid path: " + path);

        final String name = el[1];
        StatisticFragment f = null;
        if (name.equals(PATH_STAT_CUSTOM))
            f = new StatisticFragmentCustom();
        else
            f = new StatisticFragment();

        if (el[0].equals(PATH_CAT))
            f.setExpensesDates(expensesdates);
        else if (el[0].equals(PATH_TAG))
            f.setExpensesDates(expensestagdates);
        else
            throw new IllegalArgumentException("invalid path: " + path);

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
            f.setPeriod(AdvancedStatisticAdapter.StatisticPeriod.CUSTOM);

        f.setTags(tags);
        f.setCostItems(costitems);
        f.initialize(c, el[0].equals(PATH_CAT));
        return f;
    }
}
