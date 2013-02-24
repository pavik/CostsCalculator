/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import android.annotation.SuppressLint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Service provides set of static methods for data formatting
 * 
 * <pre>
 * Usage:
 * {
 *     DataFormatService.some_method();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class DataFormatService
{
    /**
     * Format price value to string with grouping by thousands
     */
    public static String formatPrice(double price)
    {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(true);
        return df.format(price);
    }

    /**
     * Format date to string
     */
    public static String formatDate(Date d)
    {
        return SimpleDateFormat.getDateInstance().format(d);
    }

    /**
     * Format time to string
     */
    public static String formatTime(Date d)
    {
        return SimpleDateFormat.getTimeInstance().format(d);
    }

    /**
     * Format year and month to string
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatMonth(Date d)
    {
        return new SimpleDateFormat("MMMM yyyy").format(d);
    }

    /**
     * File name contains current time info with extension .JSON
     */
    @SuppressLint("SimpleDateFormat")
    public static String getBackupFileNameNow()
    {
        String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                .format(Calendar.getInstance().getTime());
        return fileName + ".JSON";
    }

    public static String eraseAllCommasAndSpaces(String s)
    {
        if (s != null)
        {
            StringBuilder sb = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); ++i)
                if (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')
                    sb.append(s.charAt(i));

            return sb.toString();
        }

        return s;
    }
}
