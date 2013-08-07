
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

        if (periodtype == Calendar.WEEK_OF_YEAR)
        {
            Calendar calbeg = Calendar.getInstance();
            Calendar calend = Calendar.getInstance();
            calbeg.setTime(begin);
            calend.setTime(end);
            return calbeg.get(Calendar.WEEK_OF_YEAR) == calend
                    .get(Calendar.WEEK_OF_YEAR);
        }
        else if (periodtype == Calendar.MONTH)
            return (begin.getMonth() == end.getMonth() && begin.getYear() == end
                    .getYear());
        else if (periodtype == Calendar.YEAR)
            return begin.getYear() == end.getYear();
        else
            throw new IllegalArgumentException("unexpected periodtype: "
                    + periodtype);
    }
}
