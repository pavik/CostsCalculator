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
     * Format price value to string without grouping by thousands
     */
    public static String priceToString(double price)
    {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        return df.format(price).replace(',', '.');
    }

    /**
     * Format date to string for the default locale
     */
    public static String formatDate(Date d)
    {
        return SimpleDateFormat.getDateInstance().format(d);
    }

    /**
     * Format time to string for the default locale
     */
    public static String formatTime(Date d)
    {
        return SimpleDateFormat.getTimeInstance().format(d);
    }

    /**
     * Format datetime to string for the default locale
     */
    public static String formatDateTime(Date d)
    {
        return SimpleDateFormat.getDateTimeInstance().format(d);
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

    public static String eraseAllCommasAndSpaces(String str)
    {
        if (str != null)
        {
            String s = str.replace(',', '.');
            StringBuilder sb = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); ++i)
            {
                if (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.' || s.charAt(i) == '-')
                    sb.append(s.charAt(i));
                else; // skip char
            }

            return sb.toString();
        }
        else
            return str;
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatWeek(Date first, Date second)
    {
        String week = new SimpleDateFormat("w").format(first);
        return String.format("%s (%s - %s)", week, formatDate(first),
                formatDate(second));
    }

    public static String formatMonth(Date first, Date second)
    {
        return formatMonth(first);
    }

    @SuppressWarnings("deprecation")
    public static String formatYear(Date first, Date second)
    {
        return Integer.toString(first.getYear() + 1900);
    }

    public static String formatCustom(Date first, Date second)
    {
        return String.format("%s - %s", formatDate(first), formatDate(second));
    }
}
