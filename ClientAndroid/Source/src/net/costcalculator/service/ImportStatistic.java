/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.service;

/**
 * Provides information about import results.
 * 
 * @author Aliaksei Plashchanski
 * 
 */
public class ImportStatistic
{
    public ImportStatistic()
    {
        ci_total = 0;
        ci_errors = 0;
        ci_ignored_existent = 0;
        ci_imported_new = 0;
        ci_imported_overwritten = 0;
        cir_total = 0;
        cir_errors = 0;
        cir_ignored_existent = 0;
        cir_imported_new = 0;
        cir_imported_overwritten = 0;       
    }
    
    public int ci_total;
    public int ci_errors;
    public int ci_ignored_existent;
    public int ci_imported_new;
    public int ci_imported_overwritten;
    
    public int cir_total;
    public int cir_errors;
    public int cir_ignored_existent;
    public int cir_imported_new;
    public int cir_imported_overwritten;
}
