/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
     * Format year and month to string
     */
    public static String formatMonth(Date d)
    {
        return new SimpleDateFormat("MMMM yyyy").format(d);
    }
}
