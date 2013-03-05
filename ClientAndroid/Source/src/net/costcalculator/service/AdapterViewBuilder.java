/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import android.view.View;
import android.view.ViewGroup;

/**
 * Provides interface for building different views in one adapter.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public interface AdapterViewBuilder<ObjectType>
{
    public View getView(int pos, View convertView, ViewGroup parent,
            ObjectType obj);
}
