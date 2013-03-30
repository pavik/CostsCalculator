/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Service provides access to shared preferences, convenient way to store
 * key/value pairs.
 * 
 * <pre>
 * Usage:
 * {
 *     PreferencesService s = new PreferencesService(context);
 *     s.some_method();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class PreferencesService
{
    // list of keys
    final static public String ACCESS_KEY_NAME    = "ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    final static public String BACKUP_INTERVAL    = "BACKUP_INTERVAL";

    // access methods
    public void set(String key, String val)
    {
        SharedPreferences prefs = context_.getSharedPreferences(
                ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(key, val);
        edit.commit();
    }

    // returns null if key not found
    public String get(String key)
    {
        SharedPreferences prefs = context_.getSharedPreferences(
                ACCOUNT_PREFS_NAME, 0);
        return prefs.getString(key, null);
    }

    public void remove(String key)
    {
        SharedPreferences prefs = context_.getSharedPreferences(
                ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }

    // implementation
    public PreferencesService(Context c)
    {
        context_ = c.getApplicationContext();
    }

    private Context             context_;
    final static private String ACCOUNT_PREFS_NAME = "expenses_options";
}
