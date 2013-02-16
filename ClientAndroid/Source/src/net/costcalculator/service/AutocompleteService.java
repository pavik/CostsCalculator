/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

import java.util.List;

import net.costcalculator.util.LOG;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Service provides static methods for creating simple autocomplete adapters
 * 
 * <pre>
 * Usage:
 * {
 *     AutocompleteService.some_method();
 * }
 * </pre>
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class AutocompleteService
{
    public static ArrayAdapter<String> createCurrenciesAdapter(Context c)
    {
        LOG.T("AutocompleteService::createCurrenciesAdapter");

        CostItemsService cis = new CostItemsService(c);
        List<String> currencies = cis.getAllDistinctCurrencies();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
                android.R.layout.simple_list_item_1, currencies);
        cis.release();
        return adapter;
    }

    public static ArrayAdapter<String> createTagsAdapter(Context c)
    {
        LOG.T("AutocompleteService::createTagsAdapter");

        CostItemsService cis = new CostItemsService(c);
        List<String> tags = cis.getAllDistinctTags();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
                android.R.layout.simple_list_item_1, tags);
        cis.release();
        return adapter;
    }

    public static ArrayAdapter<String> createCommentsAdapter(Context c)
    {
        LOG.T("AutocompleteService::createCommentsAdapter");

        CostItemsService cis = new CostItemsService(c);
        List<String> comments = cis.getAllDistinctComments();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
                android.R.layout.simple_list_item_1, comments);
        cis.release();
        return adapter;
    }
}
