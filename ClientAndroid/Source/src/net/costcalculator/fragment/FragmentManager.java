/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.fragment;

import java.util.Hashtable;

import net.costcalculator.util.LOG;

import android.support.v4.app.Fragment;

/**
 * FragmentManager is responsible for storing alive fragments
 * 
 * <pre>
 * Usage:
 * {
 *     // add to the fragment lifecycle callbacks
 *     @Override
 *     public void onCreate(Bundle savedInstanceState)
 *     {
 *         super.onCreate(savedInstanceState);
 *         FragmentManager.instance().put(TAG, this);
 *     }
 *    
 *     @Override
 *     public void onDestroy()
 *     {
 *         super.onDestroy();
 *         FragmentManager.instance().remove(TAG);
 *     }
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class FragmentManager
{
    public static FragmentManager instance()
    {
        if (instance_ == null)
            instance_ = new FragmentManager();
        return instance_;
    }

    public static void destroyInstance()
    {
        instance_ = null;
    }

    public void put(String tag, Fragment f)
    {
        if (tag == null || f == null)
            throw new IllegalArgumentException("invalid input");
        else
            fragments_.put(tag, f);
    }

    public void remove(String tag)
    {
        if (tag == null)
            throw new IllegalArgumentException("invalid input");

        Fragment f = fragments_.remove(tag);
        if (f == null)
            LOG.E("FragmentManager::remove - fragment not found by tag " + tag);
    }

    public Fragment find(String tag)
    {
        if (tag == null)
            throw new IllegalArgumentException("invalid input");
        else
            return fragments_.get(tag);
    }

    public <T extends Fragment> T find(String tag, Class<T> type)
    {
        if (tag == null)
            throw new IllegalArgumentException("invalid input");
        else
        {
            Fragment f = find(tag);
            if (f != null)
                return type.cast(f);
        }
        return null;
    }

    public void removeAll()
    {
        fragments_.clear();
    }

    private FragmentManager()
    {
        fragments_ = new Hashtable<String, Fragment>();
    }

    private Hashtable<String, Fragment> fragments_;
    private static FragmentManager      instance_ = null;
}
