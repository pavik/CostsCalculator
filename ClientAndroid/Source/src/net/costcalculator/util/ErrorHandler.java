/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.util;

import android.content.Context;

/**
 * Class is responsible for error handling.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ErrorHandler
{
    public static void handleException(Exception e, Context c)
    {
        LOG.E(e.getMessage() == null ? e.toString() : e.getMessage());
        StackTraceElement[] st = e.getStackTrace();
        for (int i = 0; i < st.length; ++i)
            LOG.E(st[i].getMethodName());

        // Intent intent = new Intent(c, ErrorHandlerActivity.class);
        // c.startActivity(intent);
    }
}
