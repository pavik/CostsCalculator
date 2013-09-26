/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.Hashtable;

import net.costcalculator.util.LOG;

public class DataCacheService
{
    public static DataCacheService instance()
    {
        if (instance_ == null)
            instance_ = new DataCacheService();
        return instance_;
    }

    public static void destroyInstance()
    {
        instance_ = null;
    }

    public void put(String tag, Object obj)
    {
        if (tag == null || obj == null)
            throw new IllegalArgumentException("invalid input");
        else
            objects_.put(tag, obj);
    }

    public void remove(String tag)
    {
        if (tag == null)
            throw new IllegalArgumentException("invalid input");

        Object obj = objects_.remove(tag);
        if (obj == null)
            LOG.E("DataCacheService::remove - object not found by tag " + tag);
    }

    public Object find(String tag)
    {
        if (tag == null)
            throw new IllegalArgumentException("invalid input");
        else
            return objects_.get(tag);
    }

    public <T extends Object> T find(String tag, Class<T> type)
    {
        if (tag == null)
            throw new IllegalArgumentException("invalid input");
        else
        {
            Object obj = find(tag);
            if (obj != null)
                return type.cast(obj);
        }
        return null;
    }

    private DataCacheService()
    {
        objects_ = new Hashtable<String, Object>();
    }

    private Hashtable<String, Object> objects_;
    private static DataCacheService   instance_ = null;
}
