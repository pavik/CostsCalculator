/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.util;

import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

/**
 * Class collects application debug information.
 * 
 * @author Aliaksei Plashchanski
 *
 */
public class LOG
{
    public static final String TAG_T = "CC_TRACE";
    public static final String TAG_E = "CC_ERROR";
    public static final String TAG_D = "CC_DEBUG";
    public static final int MAX_LOG_SIZE = 100;
    
    public static ArrayList<LogItem> getLog()
    {
        ArrayList<LogItem> log = new ArrayList<LogItem>();
        if (log_ == null)
            return log;

        synchronized (log_)
        {
            log.addAll(log_);
        }
        return log;
    }
    
    public static void T(String msg)
    {
        LogItem li = new LogItem();
        li.tag = TAG_T;
        li.msg = msg;
        add(li);
        
        Log.i(TAG_T, msg);
    }
    
    public static void E(String msg)
    {
        LogItem li = new LogItem();
        li.tag = TAG_E;
        li.msg = msg;
        add(li);
        
        Log.e(TAG_E, msg);
    }
    
    public static void D(String msg)
    {
        LogItem li = new LogItem();
        li.tag = TAG_D;
        li.msg = msg;
        add(li);
        
        Log.d(TAG_D, msg);
    }
    
    public static void INITIALIZE()
    {
        size_ = 0;
        log_ = new LinkedList<LogItem>();
    }
    
    public static void RELEASE()
    {
        if (log_ == null)
            return;
        
        synchronized (log_)
        {
            log_ = null;
            size_ = 0;
        }
    }
    
    private static void add(LogItem item)
    {
        if (log_ == null)
            return;

        synchronized (log_)
        {
            log_.add(item);
            ++size_;
            if (size_ > MAX_LOG_SIZE)
            {
                log_.removeFirst();
                --size_;
            }
        }
    }
    
    private static LinkedList<LogItem> log_ = null;
    private static int size_ = 0;
}