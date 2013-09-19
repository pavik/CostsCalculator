
package net.costcalculator.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
    public static int getDaysCount(Date begin, Date end)
    {
        if (begin == null || end == null)
            throw new NullPointerException("invalid input");
        int firstday = (int) (begin.getTime() / 86400000);
        int lastday = (int) (end.getTime() / 86400000);

        return Math.abs(lastday - firstday);
    }

    public static boolean samePeriod(Date begin, Date end, int periodtype)
    {
        if (begin == null || end == null)
            throw new NullPointerException("invalid input");

        Calendar calbeg = Calendar.getInstance();
        Calendar calend = Calendar.getInstance();
        calbeg.setTime(begin);
        calend.setTime(end);

        if (periodtype == Calendar.WEEK_OF_YEAR)
            return calbeg.get(Calendar.WEEK_OF_YEAR) == calend
                    .get(Calendar.WEEK_OF_YEAR);
        else if (periodtype == Calendar.MONTH)
            return (calbeg.get(Calendar.MONTH) == calend.get(Calendar.MONTH) && calbeg
                    .get(Calendar.YEAR) == calend.get(Calendar.YEAR));
        else if (periodtype == Calendar.YEAR)
            return calbeg.get(Calendar.YEAR) == calend.get(Calendar.YEAR);
        else
            throw new IllegalArgumentException("unexpected periodtype: "
                    + periodtype);
    }

}
